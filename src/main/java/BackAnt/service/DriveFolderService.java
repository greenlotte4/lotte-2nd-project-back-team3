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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        String parentFolderPath = "/uploads/drive/my"; // 기본 경로
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


    //마이드라이브폴더파일조회
    public Map<String, Object> MyDriveView() {
        List<DriveFolderDocument> MyDriveFolders = driveFolderRepository.findFirstWithFolders();
        List<DriveFileEntity> MyDriveFiles = driveFileRepository.findByDriveFolderIdIsNullAndDriveIsDeleted(0);
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
//마이드라이브선택된폴더파일조회
    public Map<String, Object> MyDriveSelectView(String driveFolderId) {
        List<DriveFolderDocument> MyDriveFolders = driveFolderRepository.findWithSelectFolders(driveFolderId);
        List<DriveFileEntity> MyDriveFiles = driveFileRepository.findByDriveFolderIdAndDriveIsDeleted(driveFolderId,0);

        log.info("파일...나와..? 마요야?.. : " + MyDriveFiles);
        Map<String, Object> response = new HashMap<>();
        response.put("folders", MyDriveFolders);  // MyDriveFolders를 "folders"라는 키로 추가
        response.put("files", MyDriveFiles);     // MyDriveFiles를 "files"라는 키로 추가


//        List<MyDriveViewDTO> myDriveViewDTOList = MyDriveFolders.stream()
//                .map(folder -> modelMapper.map(folder, MyDriveViewDTO.class))
//                .collect(Collectors.toList());


        return response;
    }

    //휴지통폴더파일조회
    public Map<String, Object> MyTrashView() {
        List<DriveFolderDocument> MyTrashFolders = driveFolderRepository.findFirstWithDeleteFolders();
        List<DriveFileEntity> MyTrashFiles = driveFileRepository.findByDriveFolderIdIsNullAndDriveIsDeleted(1);
        log.info("휴지통 파일...나와..? 야옹.. : " + MyTrashFiles);

        Map<String, Object> response = new HashMap<>();
        response.put("folders", MyTrashFolders);  // MyDriveFolders를 "folders"라는 키로 추가
        response.put("files", MyTrashFiles);     // MyDriveFiles를 "files"라는 키로 추가

        return response;
    }

    //휴지통선택된폴더파일조회
    public Map<String, Object> MyTrashSelectView(String driveFolderId) {
        List<DriveFolderDocument> MyDriveFolders = driveFolderRepository.findWithSelectDeleteFolders(driveFolderId);
        List<DriveFileEntity> MyDriveFiles = driveFileRepository.findByDriveFolderIdAndDriveIsDeleted(driveFolderId,1);

        log.info("오잉 : " + MyDriveFolders);
        log.info("오이잉 :" + MyDriveFiles);
        Map<String, Object> response = new HashMap<>();
        response.put("folders", MyDriveFolders);  // MyDriveFolders를 "folders"라는 키로 추가
        response.put("files", MyDriveFiles);     // MyDriveFiles를 "files"라는 키로 추가



        return response;
    }




    public DriveNewFolderInsertDTO DriveFolderFind(DriveFolderNameDTO driveFolderNameDTO) {
        Optional<DriveFolderDocument> driveFolder = driveFolderRepository.findById(driveFolderNameDTO.getDriveFolderId());
        log.info("호오유유유유 : " + driveFolder);
        if (driveFolder.isPresent()) {
            DriveFolderDocument folder = driveFolder.get();
            folder.setDriveFolderName(driveFolderNameDTO.getDriveFolderName());
            log.info("이게이름이나와야돼 : " + folder);
            return modelMapper.map(driveFolderRepository.save(folder), DriveNewFolderInsertDTO.class);
        }
        return null;
    }
//휴지통으로
    public ResponseEntity<?> ToOneMyTrash(String driveFolderNameId , Integer selectedDriveFileId) throws IOException {
        boolean isFileUpdated = false; // 파일 처리 여부
        boolean isFolderUpdated = false; // 폴더 처리 여부

        // 1. 파일 ID 처리
        if (selectedDriveFileId != 0) {

                Optional<DriveFileEntity> OneFile = driveFileRepository.findById(selectedDriveFileId);
                if (OneFile.isPresent() && OneFile.get().getDriveIsDeleted() == 0) {
                    OneFile.get().setDriveIsDeleted(1);
                    OneFile.get().setDriveFileDeletedAt(LocalDateTime.now());
                    driveFileRepository.save(OneFile.get());
                    isFileUpdated = true;

            }
        }
        // 2. 폴더 ID 처리
        if (driveFolderNameId != null) {
            Optional<DriveFolderDocument> driveFolderPath = driveFolderRepository.finddriveFolderNameById(driveFolderNameId);
            if (driveFolderPath.isPresent()) {
                String path = driveFolderPath.get().getDriveFolderPath();
                List<DriveFolderDocument> folders = driveFolderRepository.findBydriveFolderPathStartingWith(path);
                List<DriveFileEntity> files = driveFileRepository.findBydriveFilePathStartingWith(path);

                for (DriveFolderDocument folder : folders) {
                    folder.setDriveFolderIsDeleted(1);
                    folder.setDriveFolderDeletedAt(LocalDateTime.now());
                }
                driveFolderRepository.saveAll(folders);

                for (DriveFileEntity file : files) {
                    file.setDriveIsDeleted(1);
                    file.setDriveFileDeletedAt(LocalDateTime.now());
                }
                driveFileRepository.saveAll(files);

                isFolderUpdated = true;
                log.info("폴더 ID {} 및 하위 항목 처리 완료", driveFolderNameId);
            } else {
                log.warn("폴더 ID {}는 존재하지 않음", driveFolderNameId);
            }
        }

        // 3. 결과 반환
        if (isFileUpdated && isFolderUpdated) {
            return ResponseEntity.ok("파일과 폴더가 성공적으로 휴지통으로 이동되었습니다.");
        } else if (isFileUpdated) {
            return ResponseEntity.ok("파일이 성공적으로 휴지통으로 이동되었습니다.");
        } else if (isFolderUpdated) {
            return ResponseEntity.ok("폴더와 하위 항목이 성공적으로 휴지통으로 이동되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일과 폴더를 찾을 수 없습니다.");
        }
    }
//복원하기
    public ResponseEntity<?> ToMyDrive(List<String> driveFolderIdList) throws IOException {
        boolean isUpdated = false; // 업데이트 여부를 추적하기 위한 변수
        log.info("이건 몇개나오냐 : "  + driveFolderIdList);
        for (String driveFolderId : driveFolderIdList) {
            log.info("이거 몇개나와? : " + driveFolderId);
            Optional<DriveFolderDocument> driveFolderPath = driveFolderRepository.finddriveFolderNameById(driveFolderId);
            if(driveFolderPath.isPresent()) {
                String path = driveFolderPath.get().getDriveFolderPath();
                List<DriveFolderDocument> folders = driveFolderRepository.findBydriveFolderPathStartingWith(path);
                List<DriveFileEntity> files = driveFileRepository.findBydriveFilePathStartingWith(path);
                for (DriveFolderDocument folder : folders) {
                    if (folder.getDriveFolderIsDeleted() == 1) { // 복원할 대상만
                        folder.setDriveFolderIsDeleted(0);
                        folder.setDriveFolderDeletedAt(LocalDateTime.now());
                    }
                }
                driveFolderRepository.saveAll(folders);

                for (DriveFileEntity file : files) {
                    if (file.getDriveIsDeleted() == 1) { // 복원할 대상만
                        file.setDriveIsDeleted(0);
                        file.setDriveFileDeletedAt(LocalDateTime.now());
                    }
                }
                driveFileRepository.saveAll(files);

                isUpdated = true;
            }
        }

        if (isUpdated) {
            return ResponseEntity.ok("Moved to trash successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Drive folder not found.");
        }
    }


    public ResponseEntity<?> ToMyTrash(List<String> driveFolderIdList ,List<Integer> selectedDriveFileIds) throws IOException {
        boolean isUpdated = false; // 업데이트 여부를 추적하기 위한 변수

        // 1. 파일 ID 처리
        if (selectedDriveFileIds != null && !selectedDriveFileIds.isEmpty()) {
            for (int driveFileId : selectedDriveFileIds) {
                Optional<DriveFileEntity> OneFile = driveFileRepository.findById(driveFileId);
                if (OneFile.isPresent() && OneFile.get().getDriveIsDeleted() == 0) {
                    OneFile.get().setDriveIsDeleted(1);
                    OneFile.get().setDriveFileDeletedAt(LocalDateTime.now());
                    driveFileRepository.save(OneFile.get());
                    isUpdated = true;
                }
            }
        }

        // 2. 폴더 ID 처리
        if (driveFolderIdList != null && !driveFolderIdList.isEmpty()) {
            for (String driveFolderId : driveFolderIdList) {
                Optional<DriveFolderDocument> driveFolderPath = driveFolderRepository.finddriveFolderNameById(driveFolderId);
                if (driveFolderPath.isPresent()) {
                    String path = driveFolderPath.get().getDriveFolderPath();
                    List<DriveFolderDocument> folders = driveFolderRepository.findBydriveFolderPathStartingWith(path);
                    List<DriveFileEntity> files = driveFileRepository.findBydriveFilePathStartingWith(path);

                    // 폴더 상태 변경
                    for (DriveFolderDocument folder : folders) {
                        if (folder.getDriveFolderIsDeleted() == 0) {
                            folder.setDriveFolderIsDeleted(1);
                            folder.setDriveFolderDeletedAt(LocalDateTime.now());
                        }
                    }
                    driveFolderRepository.saveAll(folders);

                    // 파일 상태 변경
                    for (DriveFileEntity file : files) {
                        if (file.getDriveIsDeleted() == 0) {
                            file.setDriveIsDeleted(1);
                            file.setDriveFileDeletedAt(LocalDateTime.now());
                        }
                    }
                    driveFileRepository.saveAll(files);

                    isUpdated = true;
                }
            }
        }

        // 3. 결과 반환
        if (isUpdated) {
            return ResponseEntity.ok("Moved to trash successfully.");
        } else if ((driveFolderIdList == null || driveFolderIdList.isEmpty()) &&
                (selectedDriveFileIds == null || selectedDriveFileIds.isEmpty())) {
            return ResponseEntity.badRequest().body("No IDs provided.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Drive folder or files not found.");
        }
    }
}
