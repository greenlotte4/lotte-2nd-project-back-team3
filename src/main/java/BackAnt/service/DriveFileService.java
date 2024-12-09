package BackAnt.service;

import BackAnt.document.page.drive.DriveFolderDocument;
import BackAnt.dto.drive.DriveNewFileInsertDTO;
import org.springframework.http.MediaType;
import BackAnt.entity.DriveFileEntity;
import BackAnt.repository.DriveFileRepository;
import BackAnt.repository.mongoDB.drive.DriveFolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Log4j2
@Service
public class DriveFileService {

    private final DriveFileRepository driveFileRepository;
    private final DriveFolderRepository driveFolderRepository;
    private final ModelMapper modelMapper;
    private final String USER_DIR = System.getProperty("user.dir"); // 현재 위치에서 /uploads를 붙혀주기때문에 배포 시 문제 없음

    //상위추적경로 메서드
    public String buildFolderPath(String driveFolderId, String parentFolderPath) {
        if (driveFolderId == null) {
            throw new IllegalArgumentException("driveFolderId는 null일 수 없습니다.");
        }

        // 경로를 누적할 스택
        Deque<String> folderStack = new LinkedList<>();

        // MongoDB에서 최상위 폴더까지 반복적으로 부모 폴더를 조회
        while (driveFolderId != null) {
            log.info("현재 추적 중인 폴더 ID: {}", driveFolderId);

            // 데이터베이스에서 현재 폴더 조회
            Optional<DriveFolderDocument> folderOpt = driveFolderRepository.finddriveFolderNameById(driveFolderId);
            if (folderOpt.isPresent()) {
                DriveFolderDocument folder = folderOpt.get();

                // 폴더 이름을 스택에 추가
                folderStack.push(driveFolderId);

                // 부모 폴더 ID를 얻음, 없으면 null
                driveFolderId = folder.getDriveParentFolderId(); // 필드가 없으면 null 반환
                log.info("이 ID는 먼데? : " + driveFolderId);
            } else {
                // 폴더를 찾을 수 없을 경우 예외 발생
                throw new RuntimeException("폴더를 찾을 수 없습니다: " + driveFolderId);
            }
        }

        // 기본 경로에 상위 폴더들을 연결 (fullPath는 초기값으로 parentFolderPath를 가짐)
        StringBuilder fullPath = new StringBuilder(parentFolderPath);
        while (!folderStack.isEmpty()) {
            fullPath.append("/").append(folderStack.pop());
        }

        log.info("최종 생성된 경로: {}", fullPath);
        return fullPath.toString();
    }

    //파일 등록
    public List<DriveNewFileInsertDTO> fileInsert(DriveNewFileInsertDTO driveNewFileInsertDTO) {
        String driveFolderId = driveNewFileInsertDTO.getDriveFolderId(); // 상위 폴더 ID
        String newFileId = UUID.randomUUID().toString();
        List<MultipartFile> driveFiles = driveNewFileInsertDTO.getDriveFiles();
        String parentFolderPath = "/uploads/drive";

        // 상위 폴더 경로 계산
        if (driveFolderId != null) {
            parentFolderPath = buildFolderPath(driveFolderId, parentFolderPath); // 상위 경로 추적
        }
        log.info("왕라라랄ㅇ으유유융 : " + parentFolderPath);

        if (driveFiles == null || driveFiles.isEmpty()) {
            return Collections.emptyList();  // 파일이 없으면 빈 리스트 반환
        }

        List<DriveNewFileInsertDTO> fileDTOs = new ArrayList<>();  // 파일 정보를 저장할 리스트



        if(driveFiles == null || driveFiles.size() == 0) {
            return null;
        }
        for(MultipartFile driveFile : driveFiles) {
            String driveFileName = driveFile.getOriginalFilename();
            if(driveFileName.contains("/")){
//                driveFileName = driveFileName.substring(0,driveFileName.lastIndexOf("."));
                driveFileName = Paths.get(driveFileName).getFileName().toString();
                newFileId = UUID.randomUUID().toString();
                long fileSize = driveFile.getSize(); // 파일 크기 (바이트 단위)
                double fileSizeInKB = fileSize / 1024.0; // 바이트를 KB로 변환
                double roundedFileSizeInKB = Math.round(fileSizeInKB * 100.0) / 100.0; // 소수점 2자리까지 반올림
            }
            log.info("이오리지날네임모야? : " + driveFileName);
            String driveFileSName = System.currentTimeMillis() + "_" + driveFileName;
            String driveFileMaker = driveNewFileInsertDTO.getDriveFileMaker();
            String ext = driveFileName.substring(driveFileName.lastIndexOf("."));
            String driveFileOName = UUID.randomUUID().toString() + ext;

            String newFolderPath = parentFolderPath + "/" + newFileId;
            Path folderPath = Paths.get(USER_DIR + newFolderPath); // 폴더 경로 생성
            Path folderP = Paths.get(USER_DIR + parentFolderPath);
            Path folderPaths = Paths.get(USER_DIR+"/uploads/drive/");

            long fileSize = driveFile.getSize(); // 파일 크기 (바이트 단위)
            double fileSizeInKB = fileSize / 1024.0; // 바이트를 KB로 변환
            double roundedFileSizeInKB = Math.round(fileSizeInKB * 100.0) / 100.0; // 소수점 2자리까지 반올림
            log.info("파일 사이즈: {} KB", roundedFileSizeInKB); // 파일 사이즈 출력

            // 폴더가 없으면 디렉터리 생성
            try {
                Files.createDirectories(folderP); // 경로에 디렉터리 생성
            } catch (IOException e) {
                log.error("디렉터리 생성 실패: {}", folderP.toString(), e);
                throw new RuntimeException("디렉터리 생성 실패", e);
            }

            // 실제 파일 저장 경로 (폴더 내에 파일 생성ID으로 저장)
            Path filePath = folderP.resolve(newFileId); // 최종 저장 경로

            // 파일 저장
            try {
                Files.copy(driveFile.getInputStream(), filePath);  // 파일 복사
            } catch (IOException e) {
                log.error("파일 저장 실패: {}", filePath.toString(), e);
                throw new RuntimeException("파일 저장 실패", e);
            }
            // folderId가 있을 경우 처리
            String folderId = driveNewFileInsertDTO.getDriveFolderId(); // folderId 추출
            DriveFileEntity.DriveFileEntityBuilder drivefileBuilder = DriveFileEntity.builder()
                    .driveFileOName(driveFileOName)
                    .driveFileSName(driveFileSName)
                    .driveFileMaker(driveFileMaker)
                    .driveFileCreatedAt(LocalDateTime.now())
                    .driveFilePath(newFolderPath)
                    .driveFileSize(roundedFileSizeInKB);

            // folderId가 있을 경우에만 추가
            if (folderId != null) {
                drivefileBuilder.driveFolderId(folderId); // DriveFileEntity에 folderId 설정
            }

            DriveFileEntity drivefile = drivefileBuilder.build();

            log.info("파일 정보: {}", drivefile);

            log.info("file이 먼데! : " + drivefile);

            fileDTOs.add(modelMapper.map(driveFileRepository.save(drivefile), DriveNewFileInsertDTO.class));
        }

        return fileDTOs;

    }

    //파일 다운로드
    public ResponseEntity<Resource> MyDriveFileDownload(int driveFileId){
        try {
            // 데이터베이스에서 파일 정보 조회
            Optional<DriveFileEntity> driveFile = driveFileRepository.findById(driveFileId);
            if (!driveFile.isPresent()) {
                throw new RuntimeException("파일 정보가 존재하지 않습니다: " + driveFileId);
            }

            // 파일 경로 생성
            String driveFilePath = driveFile.get().getDriveFilePath();
            Path filePath = Paths.get(USER_DIR + driveFilePath).normalize();

            // 파일 존재 여부 확인
            if (!Files.exists(filePath)) {
                throw new RuntimeException("파일이 존재하지 않습니다: " + filePath);
            }

            // Resource 객체로 파일 로드
            Resource resource = new UrlResource(filePath.toUri());
            log.info("머라머라: {}", resource);
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("파일을 읽을 수 없거나 존재하지 않습니다: " + filePath);
            }

            // 파일 확장자 추출 (MIME 타입 설정용)
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // 기본 MIME 타입
            }

            // HTTP 응답 헤더 설정
            String fileName = filePath.getFileName().toString();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType)) // MIME 타입 지정
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"") // 다운로드 처리
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("파일 다운로드 처리 중 오류 발생", e);
        }
    }

}


