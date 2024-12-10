package BackAnt.service;

import BackAnt.document.page.drive.DriveFolderDocument;
import BackAnt.dto.drive.DriveFolderNameDTO;
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
import org.bson.types.ObjectId;


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



// 새 폴더 생성
    public DriveNewFolderInsertDTO FolderNewInsert(DriveNewFolderInsertDTO driveNewFolderInsertDTO) {
        String driveFolderId = driveNewFolderInsertDTO.getDriveFolderId(); // 상위 폴더 ID
        String newFolderId = UUID.randomUUID().toString(); // 새로운 폴더의 UUID
        log.info("새 폴더 ID: " + newFolderId);

        String parentFolderPath = "/uploads/drive"; // 기본 경로
        String USER_DIR = System.getProperty("user.dir"); // 현재 작업 디렉터리

        String driveFolderPath;

        if (driveFolderId != null && !driveFolderId.isEmpty()) {
            // 상위 폴더가 있는 경우
            Optional<DriveFolderDocument> folderOpt = driveFolderRepository.finddriveFolderNameById(driveFolderId);
            if (folderOpt.isPresent()) {
                DriveFolderDocument folder = folderOpt.get();
                driveFolderPath = folder.getDriveFolderPath();
                log.info("상위 폴더 경로: " + driveFolderPath);
            } else {
                throw new IllegalArgumentException("잘못된 상위 폴더 ID: " + driveFolderId);
            }
        } else {
            // 상위 폴더가 없는 경우 기본 경로 사용
            log.info("상위 폴더가 없습니다. 기본 경로 사용.");
            driveFolderPath = parentFolderPath;
        }

        // 새로운 폴더 경로는 UUID를 사용
        String newFolderPath = driveFolderPath + "/" + newFolderId;

        Path path = Paths.get(USER_DIR + newFolderPath);

        // 디렉터리가 존재하지 않으면 생성
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            log.error("디렉터리 생성 실패: {}", path.toString(), e);
            throw new RuntimeException("디렉터리 생성 실패", e);
        }

        // 새로운 폴더 객체 생성
        DriveFolderDocument newFolder = DriveFolderDocument.builder()
                .driveFolderName(driveNewFolderInsertDTO.getDriveFolderName()) // 사용자가 입력한 폴더 이름
                .driveFolderPath(newFolderPath) // UUID로 경로 설정
                .driveParentFolderId(driveFolderId) // 상위 폴더 ID (null일 수 있음)
                .driveFolderSize(driveNewFolderInsertDTO.getDriveFolderSize())
                .driveFolderCreatedAt(LocalDateTime.now())
                .driveFolderUpdatedAt(LocalDateTime.now())
                .driveFolderMaker(driveNewFolderInsertDTO.getDriveFolderMaker())
                .build();

        log.info("생성된 폴더 정보: " + newFolder);

        // 폴더 저장 후 DTO로 반환
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

    public DriveNewFolderInsertDTO DriveFolderFind(DriveFolderNameDTO driveFolderNameDTO) {
        Optional<DriveFolderDocument> driveFolder = driveFolderRepository.findById(driveFolderNameDTO.getDriveFolderId());
        log.info("호오유유유유 : " + driveFolder);
        if(driveFolder.isPresent()) {
            DriveFolderDocument folder = driveFolder.get();
            folder.setDriveFolderName(driveFolderNameDTO.getDriveFolderName());
            log.info("이게이름이나와야돼 : " + folder);
            return modelMapper.map(driveFolderRepository.save(folder), DriveNewFolderInsertDTO.class);
        }
        return null;
    }

    public void DriveFolderTrash(String driveFolderNameId){
        Optional<DriveFolderDocument> driveFolderPath = driveFolderRepository.finddriveFolderNameById(driveFolderNameId);
        if(driveFolderPath.isPresent()) {
            String path = driveFolderPath.get().getDriveFolderPath();
            log.info("path : " + driveFolderPath.get().getDriveFolderPath());
            List<DriveFolderDocument> folders = driveFolderRepository.findBydriveFolderPathStartingWith(path);
            log.info("folders : " + folders);


        }
//        log.info("래래래래래 : " + folders);
        // 모든 폴더의 deleted 값 업데이트
//        for (DriveFolderDocument folder : folders) {
//            folder.setDriveFolderIsDeleted(1); // 상태 변경
//            driveFolderRepository.save(folder);
        }
    }

