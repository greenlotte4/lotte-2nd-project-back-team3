package BackAnt.service;

import BackAnt.document.page.drive.DriveFolderDocument;
import BackAnt.dto.drive.DriveFileStorageDTO;
import BackAnt.dto.drive.DriveNewFileInsertDTO;
import BackAnt.entity.drive.DriveFileStorage;
import BackAnt.repository.drive.DriveFileStoarageRepository;
import org.springframework.http.MediaType;
import BackAnt.entity.drive.DriveFileEntity;
import BackAnt.repository.drive.DriveFileRepository;
import BackAnt.repository.mongoDB.drive.DriveFolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
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
    private final DriveFileStoarageRepository driveFileStoarageRepository;
    private final ModelMapper modelMapper;
    private final String USER_DIR = System.getProperty("user.dir"); // 현재 위치에서 /uploads를 붙혀주기때문에 배포 시 문제 없음


// 파일 등록
@Transactional
public List<DriveNewFileInsertDTO> fileInsert(DriveNewFileInsertDTO driveNewFileInsertDTO) {
    String driveFolderId = driveNewFileInsertDTO.getDriveFolderId(); // 상위 폴더 ID
    String name = driveNewFileInsertDTO.getDriveFileMaker();
    log.info("driveFolderId: " + driveFolderId);

    // 기본 경로 설정
    String parentFolderPath = "/uploads/drive/" + name;
    String drivePath = "";

    // 상위 폴더의 공유 여부를 판단하는 변수
    Integer sharedType = null;

    try {
        // 상위 폴더 ID가 있는 경우 경로와 공유 여부 확인
        if (driveFolderId != null && !driveFolderId.isEmpty()) {
            Optional<DriveFolderDocument> folderOpt = driveFolderRepository.finddriveFolderNameById(driveFolderId);
            if (folderOpt.isPresent()) {
                DriveFolderDocument folder = folderOpt.get();
                log.info("Found folder: " + folder);
                drivePath = folder.getDriveFolderPath(); // 상위 경로
                sharedType = folder.getDriveFolderShareType(); // 상위 폴더의 공유 여부 확인
            } else {
                log.warn("Folder ID not found: {}", driveFolderId);
            }
        } else {
            log.info("No folder ID provided. Using default path.");
        }
    } catch (Exception e) {
        log.warn("Failed to find folder or process shared folder logic: {}", e.getMessage());
    }

    List<MultipartFile> driveFiles = driveNewFileInsertDTO.getDriveFiles();
    if (driveFiles == null || driveFiles.isEmpty()) {
        return Collections.emptyList(); // 파일이 없으면 빈 리스트 반환
    }

    List<DriveNewFileInsertDTO> fileDTOs = new ArrayList<>();

    for (MultipartFile driveFile : driveFiles) {
        String driveFileName = driveFile.getOriginalFilename();
        if (driveFileName == null) continue;

        // 파일 이름 처리
        driveFileName = Paths.get(driveFileName).getFileName().toString(); // 파일 이름만 추출
        String newFileId = UUID.randomUUID().toString(); // 랜덤 ID
        String driveFileSName = System.currentTimeMillis() + "_" + driveFileName; // 시간 + 파일 이름
        String ext = driveFileName.substring(driveFileName.lastIndexOf(".")); // 확장자
        String driveFileOName = UUID.randomUUID().toString() + ext; // 랜덤 + 확장자

        // 최종 저장 경로 계산
        String newFolderPath = drivePath.isEmpty() ? parentFolderPath : drivePath;
        log.info("뉴경로 : " + newFolderPath);
        Path folderPath = Paths.get(USER_DIR + newFolderPath);
        Path filePath = folderPath.resolve(newFileId);

        // 디렉토리 생성
        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            log.error("Failed to create directory: {}", folderPath, e);
            throw new RuntimeException("Failed to create directory", e);
        }

        // 파일 저장
        try {
            Files.copy(driveFile.getInputStream(), filePath);
        } catch (IOException e) {
            log.error("Failed to save file: {}", filePath, e);
            throw new RuntimeException("Failed to save file", e);
        }

        // 엔티티 생성 및 저장
        DriveFileEntity.DriveFileEntityBuilder builder = DriveFileEntity.builder()
                .driveFileOName(driveFileOName)
                .driveFileSName(driveFileSName)
                .driveFileMaker(driveNewFileInsertDTO.getDriveFileMaker())
                .driveFileCreatedAt(LocalDateTime.now())
                .driveFilePath(newFolderPath + "/" + newFileId)
                .driveFileSize(driveFile.getSize())
                .driveFolderId(driveFolderId);

        // 상위 폴더가 공유 폴더일 경우 공유 속성 추가
        if (sharedType != null && sharedType == 1) {
            builder.driveShareType(1);
            builder.driveFileSharedAt(LocalDateTime.now());
        }

        DriveFileEntity drivefile = builder.build();
        fileDTOs.add(modelMapper.map(driveFileRepository.save(drivefile), DriveNewFileInsertDTO.class));

        DriveFileStorage driveStorage =  driveFileStoarageRepository.findByUserId(driveNewFileInsertDTO.getDriveFileMaker());
        log.info("머냐냐?? : " + driveStorage);
        if(driveStorage == null){
            driveStorage = DriveFileStorage.builder()
                    .userId(driveNewFileInsertDTO.getDriveFileMaker())
                    .driveFileSize(driveFile.getSize())
                    .driveFileLimitSize(1073741824L) //1GB (무료회원 기준)
                    .driveSize("1GB")
                    .build();

        }else {
            driveStorage.setDriveFileSize(driveStorage.getDriveFileSize() + drivefile.getDriveFileSize());

        }
        driveFileStoarageRepository.save(driveStorage);
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
                String oName = driveFile.get().getDriveFileOName();
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

                String contentType = Files.probeContentType(filePath); // 경로에서 MIME 타입 조회 실패 가능
                if (contentType == null) {
                    contentType = URLConnection.guessContentTypeFromName(oName); // 이름에서 추론
                }
                if (contentType == null) {
                    contentType = "application/octet-stream"; // 기본 MIME 타입
                }

                // HTTP 응답 헤더 설정
                String fileName = filePath.getFileName().toString();
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType)) // MIME 타입 지정
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + oName + "\"")
                        .body(resource);
            } catch (Exception e) {
                throw new RuntimeException("파일 다운로드 처리 중 오류 발생", e);
            }
        }

        public DriveFileStorageDTO SelectDriveTotalSize(String uid, int rate){
            DriveFileStorage storage = driveFileStoarageRepository.findByUserId(uid);
            double driveFileSize = (storage != null) ? storage.getDriveFileSize() : 0.0;
            Long driveFileLimitSize = (storage != null) ? storage.getDriveFileLimitSize() : 1073741824L;
            DriveFileStorageDTO driveStoragedto = DriveFileStorageDTO.builder()
                    .driveFileSize(driveFileSize)
                    .driveFileLimitSize(driveFileLimitSize)
                    .build();
            log.info("먀모먀모먀 : "+driveStoragedto);
            return driveStoragedto;
        }

    }


