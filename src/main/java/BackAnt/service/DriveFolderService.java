package BackAnt.service;

import BackAnt.document.page.drive.DriveFolderDocument;
import BackAnt.dto.drive.DriveNewFolderInsertDTO;
import BackAnt.dto.drive.MyDriveViewDTO;
import BackAnt.entity.DriveFileEntity;
import BackAnt.repository.DriveFileRepository;
import BackAnt.repository.mongoDB.drive.DriveFolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class DriveFolderService {

    private final DriveFolderRepository driveFolderRepository;
    private final ModelMapper modelMapper;
    private final String USER_DIR = System.getProperty("user.dir"); // 현재 위치에서 /uploads를 붙혀주기때문에 배포 시 문제 없음
    private final DriveFileRepository driveFileRepository;


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

//                // 폴더 이름을 스택에 추가
//                folderStack.push(folder.getDriveFolderName());
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

    //새폴더생성
    public DriveNewFolderInsertDTO FolderNewInsert(DriveNewFolderInsertDTO driveNewFolderInsertDTO) {
        String driveFolderId = driveNewFolderInsertDTO.getDriveFolderId(); // 상위 폴더 ID
        String newFolderId = UUID.randomUUID().toString();
        log.info("dhkfkfkfkfk : " + newFolderId);
        String parentFolderPath = "/uploads/drive";

        // 상위 폴더 경로 계산
        if (driveFolderId != null) {
            parentFolderPath = buildFolderPath(driveFolderId, parentFolderPath); // 상위 경로 추적
        }

        // 새로운 폴더 경로 생성
        String newFolderName = driveNewFolderInsertDTO.getDriveFolderName();
        String newFolderPaths = parentFolderPath + "/" + newFolderName;
        String newFolderPath = parentFolderPath + "/" + newFolderId;

        // 실제 서버 경로 (user.dir을 기반으로 경로 생성)
        String USER_DIR = System.getProperty("user.dir"); // 현재 작업 디렉터리 (배포 시 경로 자동화)
        Path path = Paths.get(USER_DIR + newFolderPath); // 최종 경로에 새로운 폴더 경로 추가

        // 중복된 폴더 이름 처리
        int counter = 1;
        while (Files.exists(path)) {
            // 중복된 폴더 이름으로 변경
            newFolderName = driveNewFolderInsertDTO.getDriveFolderName() + " (" + counter + ")";
            newFolderPath = parentFolderPath + "/" + newFolderId;
            path = Paths.get(USER_DIR + parentFolderPath + "/" + newFolderId);
            counter++;
        }


        // 디렉터리가 존재하지 않으면 생성
        try {
            Files.createDirectories(path); // 경로에 디렉터리 생성
        } catch (IOException e) {
            log.error("디렉터리 생성 실패: {}", path.toString(), e);
            throw new RuntimeException("디렉터리 생성 실패", e);
        }


        // 새로운 폴더 객체 생성
        DriveFolderDocument newFolder = DriveFolderDocument.builder()
                .driveFolderName(newFolderName)
                .driveFolderPath(newFolderPath) // 경로 설정
                .driveParentFolderId(driveNewFolderInsertDTO.getDriveFolderId()) // 상위 폴더 ID
                .driveFolderSize(driveNewFolderInsertDTO.getDriveFolderSize())
                .driveFolderCreatedAt(LocalDateTime.now())
                .driveFolderUpdatedAt(LocalDateTime.now())
                .driveFolderMaker(driveNewFolderInsertDTO.getDriveFolderMaker())
                .build();

        log.info("새로 생성된 폴더: " + newFolder);

        // 새로운 폴더 저장 후 DTO로 변환
        return modelMapper.map(driveFolderRepository.save(newFolder), DriveNewFolderInsertDTO.class);
    }

//폴더파일조회
    public Map<String, Object> MyDriveView() {
        List<DriveFolderDocument> MyDriveFolders = driveFolderRepository.findFirstWithFolders();
        List<DriveFileEntity> MyDriveFiles = driveFileRepository.findBydriveFolderIdIsNull();
        log.info("파일...나와..? 야옹.. : " + MyDriveFiles);

        Map<String, Object> response = new HashMap<>();
        response.put("folders", MyDriveFolders);  // MyDriveFolders를 "folders"라는 키로 추가
        response.put("files", MyDriveFiles);     // MyDriveFiles를 "files"라는 키로 추가

        return response;
    }
//        List<MyDriveViewDTO> myDriveViewDTOList = MyDriveFolders.stream()
//                .map(folder -> modelMapper.map(folder, MyDriveViewDTO.class))
//                .collect(Collectors.toList());
//
//선택된폴더파일조회
    public  Map<String, Object> MyDriveSelectView(String driveFolderId){
        List<DriveFolderDocument> MyDriveFolders = driveFolderRepository.findWithSelectFolders(driveFolderId);
        List<DriveFileEntity> MyDriveFiles = driveFileRepository.findByDriveFolderId(driveFolderId);

        Map<String, Object> response = new HashMap<>();
        response.put("folders", MyDriveFolders);  // MyDriveFolders를 "folders"라는 키로 추가
        response.put("files", MyDriveFiles);     // MyDriveFiles를 "files"라는 키로 추가


//        List<MyDriveViewDTO> myDriveViewDTOList = MyDriveFolders.stream()
//                .map(folder -> modelMapper.map(folder, MyDriveViewDTO.class))
//                .collect(Collectors.toList());


        return response;
    }
}
