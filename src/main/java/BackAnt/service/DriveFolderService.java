package BackAnt.service;

import BackAnt.document.page.drive.DriveFolderDocument;
import BackAnt.dto.drive.*;
import BackAnt.entity.drive.DriveCollaborator;
import BackAnt.entity.drive.DriveFileEntity;
import BackAnt.entity.User;
import BackAnt.entity.drive.DriveIsStared;
import BackAnt.repository.drive.DriveFileRepository;
import BackAnt.repository.UserRepository;
import BackAnt.repository.drive.DriveCollaboratorRepository;
import BackAnt.repository.drive.DriveIsStaredRepository;
import BackAnt.repository.mongoDB.drive.DriveFolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class DriveFolderService {

    private final DriveFolderRepository driveFolderRepository;
    private final DriveCollaboratorRepository driveCollaboratorRepository;
    private final UserRepository userRepository;
    private final DriveIsStaredRepository driveIsStaredRepository;
    private final ModelMapper modelMapper;
    private final String USER_DIR = System.getProperty("user.dir"); // 현재 위치에서 /uploads를 붙혀주기때문에 배포 시 문제 없음
    private final DriveFileRepository driveFileRepository;


    // 새 폴더 생성
    public DriveNewFolderInsertDTO FolderNewInsert(DriveNewFolderInsertDTO driveNewFolderInsertDTO) {

        // 1. 로그인한 사용자 조회
        User user = userRepository.findByUid(driveNewFolderInsertDTO.getDriveFolderMaker())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        log.info("유저누군데  :" +user);

        String driveFolderId = driveNewFolderInsertDTO.getDriveFolderId(); // 상위 폴더 ID
        String name = driveNewFolderInsertDTO.getDriveFolderMaker();
        String newFolderId = UUID.randomUUID().toString(); // 새로운 폴더의 UUID
        log.info("새 폴더 ID: " + newFolderId);

        String parentFolderPath = "/uploads/drive/"+name; // 기본 경로
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
        DriveFolderDocument.DriveFolderDocumentBuilder folderBuilder = DriveFolderDocument.builder()
                .driveFolderName(driveNewFolderInsertDTO.getDriveFolderName()) // 사용자가 입력한 폴더 이름
                .driveFolderPath(newFolderPath) // UUID로 경로 설정
                .driveParentFolderId(driveFolderId) // 상위 폴더 ID (null일 수 있음)
                .driveFolderSize(driveNewFolderInsertDTO.getDriveFolderSize())
                .driveFolderCreatedAt(LocalDateTime.now())
                .driveFolderUpdatedAt(LocalDateTime.now())
                .driveFolderMaker(driveNewFolderInsertDTO.getDriveFolderMaker());

// 조건 확인: 상위 폴더가 있고 shareType이 1일 경우
        if (driveFolderId != null && !driveFolderId.isEmpty()) {
            Optional<DriveFolderDocument> folderOpt = driveFolderRepository.finddriveFolderNameById(driveFolderId);
            if (folderOpt.isPresent() && folderOpt.get().getDriveFolderShareType() == 1) {
                folderBuilder.driveFolderShareType(1);
            }
        }

// 최종적으로 빌드
        DriveFolderDocument newFolder = folderBuilder.build();

        // 폴더 저장 후 DTO로 반환
        DriveNewFolderInsertDTO driveNewFolderInsertDTO1 = modelMapper.map(driveFolderRepository.save(newFolder), DriveNewFolderInsertDTO.class);


        DriveCollaborator driveCollaborator = DriveCollaborator.builder()
                .driveFolderId(driveNewFolderInsertDTO1.getDriveFolderId())
                .driveShareType(1)
                .driveFolderShareAt(LocalDateTime.now())
                .user(user)
                .isOwner(true)
                .build();

        driveCollaboratorRepository.save(driveCollaborator);
        return driveNewFolderInsertDTO1;
    }


    //마이드라이브폴더파일 전체조회
    public Map<String, Object> MyDriveView(String uid) {
        List<DriveIsStared> MyDriveIsStared = driveIsStaredRepository.findByUserId(uid);
        log.info("좋아요한것 : "+MyDriveIsStared);
        List<DriveFolderDocument> MyDriveFolders = driveFolderRepository.findFirstWithFolders(uid);
        List<DriveFileEntity> MyDriveFiles = driveFileRepository.findByDriveFolderIdIsNullAndDriveFileMakerAndDriveIsDeleted(uid,0);
        log.info("파일...나와..? 야옹.. : " + MyDriveFiles);

        // 좋아요 데이터를 Map으로 변환 (driveFolderId -> isStared)
        Map<String, Boolean> staredMap = MyDriveIsStared.stream()
                .filter(item -> item.getDriveFolderId() != null) // 폴더 데이터만 포함
                .collect(Collectors.toMap(
                        DriveIsStared::getDriveFolderId,
                        DriveIsStared::isStared
                ));

        Map<Integer, Boolean> staredFileMap = MyDriveIsStared.stream()
                .filter(item -> item.getDriveFileId() != 0)
                .collect(Collectors.toMap(DriveIsStared::getDriveFileId, DriveIsStared::isStared));

        // 폴더 데이터와 좋아요 상태를 합친 새로운 DTO 리스트 생성
        List<MyDriveViewDTO> folderResponse = MyDriveFolders.stream()
                .map(folder -> MyDriveViewDTO.builder()
                        .driveFolderId(folder.getDriveFolderId())
                        .driveFolderName(folder.getDriveFolderName())
                        .driveFolderMaker(folder.getDriveFolderMaker())
                        .driveFolderCreatedAt(folder.getDriveFolderCreatedAt())
                        .driveFolderSize(folder.getDriveFolderSize())
                        .driveFolderShareType(folder.getDriveFolderShareType())// 예: 폴더 이름 필드
                        .driveFolderIsStared(staredMap.getOrDefault(folder.getDriveFolderId(), false)) // 좋아요 상태 (없으면 false)
                        .build()
                )
                .collect(Collectors.toList());

        List<MyDriveFileViewDTO> fileResponse = MyDriveFiles.stream()
                .map(file -> MyDriveFileViewDTO.builder()
                        .driveFolderId(file.getDriveFolderId())
                        .driveFileSName(file.getDriveFileSName())
                        .driveFileMaker(file.getDriveFileMaker())
                        .driveFileCreatedAt(file.getDriveFileCreatedAt())
                        .driveFileSize(file.getDriveFileSize())
                        .driveFileId(file.getDriveFileId())// 예: 폴더 이름 필드
                        .driveIsStarted(staredFileMap.getOrDefault(file.getDriveFileId(), false)) // 좋아요 상태 (없으면 false)
                        .build()
                )
                .collect(Collectors.toList());


        Map<String, Object> response = new HashMap<>();
        response.put("folders", folderResponse);  // MyDriveFolders를 "folders"라는 키로 추가
        response.put("files", fileResponse);     // MyDriveFiles를 "files"라는 키로 추가
        log.info("모로모로모로 : " + fileResponse);
        return response;
    }

    //        List<MyDriveViewDTO> myDriveViewDTOList = MyDriveFolders.stream()
//                .map(folder -> modelMapper.map(folder, MyDriveViewDTO.class))
//                .collect(Collectors.toList());
//
//마이드라이브 선택된폴더파일조회
    public Map<String, Object> MyDriveSelectView(String driveFolderId, String uid) {
        List<DriveFolderDocument> MyDriveFolders = driveFolderRepository.findWithSelectFolders(driveFolderId);
        List<DriveFileEntity> MyDriveFiles = driveFileRepository.findByDriveFolderIdAndDriveIsDeleted(driveFolderId,0);
        List<DriveIsStared> MyDriveIsStared = driveIsStaredRepository.findByUserId(uid);

        // 좋아요 데이터를 Map으로 변환 (driveFolderId -> isStared)
        Map<String, Boolean> staredMap = MyDriveIsStared.stream()
                .filter(item -> item.getDriveFolderId() != null) // 폴더 데이터만 포함
                .collect(Collectors.toMap(
                        DriveIsStared::getDriveFolderId,
                        DriveIsStared::isStared
                ));

        Map<Integer, Boolean> staredFileMap = MyDriveIsStared.stream()
                .filter(item -> item.getDriveFileId() != 0)
                .collect(Collectors.toMap(DriveIsStared::getDriveFileId, DriveIsStared::isStared));

        // 폴더 데이터와 좋아요 상태를 합친 새로운 DTO 리스트 생성
        List<MyDriveViewDTO> folderResponse = MyDriveFolders.stream()
                .map(folder -> MyDriveViewDTO.builder()
                        .driveFolderId(folder.getDriveFolderId())
                        .driveFolderName(folder.getDriveFolderName())
                        .driveParentFolderId(folder.getDriveParentFolderId())
                        .driveFolderMaker(folder.getDriveFolderMaker())
                        .driveFolderCreatedAt(folder.getDriveFolderCreatedAt())
                        .driveFolderSize(folder.getDriveFolderSize())
                        .driveFolderShareType(folder.getDriveFolderShareType())// 예: 폴더 이름 필드
                        .driveFolderIsStared(staredMap.getOrDefault(folder.getDriveFolderId(), false)) // 좋아요 상태 (없으면 false)
                        .build()
                )
                .collect(Collectors.toList());

        List<MyDriveFileViewDTO> fileResponse = MyDriveFiles.stream()
                .map(file -> MyDriveFileViewDTO.builder()
                        .driveFolderId(file.getDriveFolderId())
                        .driveFileSName(file.getDriveFileSName())
                        .driveFileMaker(file.getDriveFileMaker())
                        .driveFileCreatedAt(file.getDriveFileCreatedAt())
                        .driveFileSize(file.getDriveFileSize())
                        .driveFileId(file.getDriveFileId())// 예: 폴더 이름 필드
                        .driveIsStarted(staredFileMap.getOrDefault(file.getDriveFileId(), false)) // 좋아요 상태 (없으면 false)
                        .build()
                )
                .collect(Collectors.toList());

        //네비게이션용 조회
        Optional<DriveFolderDocument> currentFolderOpt = driveFolderRepository.findById(driveFolderId);
        if (currentFolderOpt.isEmpty()) {
            throw new RuntimeException("폴더를 찾을 수 없습니다: " + driveFolderId);
        }
        DriveFolderDocument currentFolder = currentFolderOpt.get();

        // Breadcrumbs 생성
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        while (currentFolder != null) {
            Map<String, String> breadcrumb = new HashMap<>();
            breadcrumb.put("id", currentFolder.getDriveFolderId());
            breadcrumb.put("name", currentFolder.getDriveFolderName());
            breadcrumbs.add(0, breadcrumb); // 부모부터 추가 (역순)
            if (currentFolder.getDriveParentFolderId() != null) {
                currentFolder = driveFolderRepository.findById(currentFolder.getDriveParentFolderId()).orElse(null);
            } else {
                currentFolder = null;
            }
        }

        // 최상위 경로 MY DRIVE 추가
        Map<String, String> rootBreadcrumb = new HashMap<>();
        rootBreadcrumb.put("id", null);
        rootBreadcrumb.put("name", "MY DRIVE");
        breadcrumbs.add(0, rootBreadcrumb);

        log.info("파일...나와..? 마요야?.. : " + MyDriveFiles);
        log.info("휴휴휴휴흏 : " + folderResponse);
        Map<String, Object> response = new HashMap<>();
        response.put("folders", folderResponse);  // MyDriveFolders를 "folders"라는 키로 추가
        response.put("files", fileResponse);     // MyDriveFiles를 "files"라는 키로 추가
        response.put("breadcrumbs", breadcrumbs);
        log.info("부스러기 : "  +breadcrumbs);



//        List<MyDriveViewDTO> myDriveViewDTOList = MyDriveFolders.stream()
//                .map(folder -> modelMapper.map(folder, MyDriveViewDTO.class))
//                .collect(Collectors.toList());


        return response;
    }

    //휴지통보기
    public Map<String, Object> MyTrashView(String uid) {
        // 1. 삭제된 폴더 가져오기 (MongoDB)
        List<DriveFolderDocument> allDeletedFolders = driveFolderRepository.findAllDeletedFolders(uid);

        // 2. 삭제된 파일 가져오기 (MySQL)
        List<DriveFileEntity> allDeletedFiles = driveFileRepository.findAllDeletedFiles(uid);

        // 폴더 ID -> 폴더 객체 매핑
        Map<String, DriveFolderDocument> folderMap = allDeletedFolders.stream()
                .collect(Collectors.toMap(DriveFolderDocument::getDriveFolderId, folder -> folder));

        // 3. 휴지통에 표시할 폴더 필터링 + 상위 폴더 이름 추가
        List<DriveFolderDocument> trashFolders = allDeletedFolders.stream()
                .peek(folder -> {
                    // 상위 폴더 이름 설정
                    String parentId = folder.getDriveParentFolderId();
                    String parentFolderName;

                    if (parentId != null) {
                        if (folderMap.containsKey(parentId)) {
                            // 상위 폴더가 삭제된 경우
                            parentFolderName = folderMap.get(parentId).getDriveFolderName();
                        } else {
                            // 상위 폴더가 삭제되지 않은 경우 DB에서 직접 조회
                            Optional<DriveFolderDocument> parentFolderOpt = driveFolderRepository.findById(parentId);
                            parentFolderName = parentFolderOpt.map(DriveFolderDocument::getDriveFolderName).orElse("내 드라이브");
                        }
                    } else {
                        parentFolderName = "내 드라이브";
                    }

                    folder.setParentFolderName(parentFolderName); // 추가된 필드에 설정
                })
                .filter(folder -> {
                    String parentId = folder.getDriveParentFolderId();

                    // 최상위 폴더는 항상 표시
                    if (parentId == null) return true;

                    // 부모 폴더 찾기
                    DriveFolderDocument parentFolder = folderMap.get(parentId);
                    if (parentFolder == null) return true;

                    // 부모 폴더가 삭제되지 않은 경우 표시
                    if (parentFolder.getDriveFolderIsDeleted() == 0) return true;

                    // 자식 폴더가 독립적으로 삭제된 경우 포함
                    return folder.getDriveFolderDeletedAt() != null
                            && folder.getDriveFolderDeletedAt().isBefore(parentFolder.getDriveFolderDeletedAt());
                })
                .collect(Collectors.toList());

        // 4. 휴지통에 표시할 파일 필터링 + 상위 폴더 이름 추가
        List<DriveFileEntity> trashFiles = allDeletedFiles.stream()
                .peek(file -> {
                    // 상위 폴더 이름 설정
                    String folderId = file.getDriveFolderId();
                    String parentFolderName;

                    if (folderId != null) {
                        if (folderMap.containsKey(folderId)) {
                            // 상위 폴더가 삭제된 경우
                            parentFolderName = folderMap.get(folderId).getDriveFolderName();
                        } else {
                            // 상위 폴더가 삭제되지 않은 경우 DB에서 직접 조회
                            Optional<DriveFolderDocument> parentFolderOpt = driveFolderRepository.findById(folderId);
                            parentFolderName = parentFolderOpt.map(DriveFolderDocument::getDriveFolderName).orElse("내 드라이브");
                        }
                    } else {
                        parentFolderName = "내 드라이브";
                    }

                    file.setParentFolderName(parentFolderName); // 추가된 필드에 설정
                })
                .filter(file -> {
                    String folderId = file.getDriveFolderId();

                    // 폴더에 속하지 않은 파일은 항상 표시
                    if (folderId == null) return true;

                    // 폴더에 속한 파일은 폴더 삭제 여부와 삭제 시점 비교
                    DriveFolderDocument parentFolder = folderMap.get(folderId);
                    if (parentFolder == null) return true;

                    // 폴더가 삭제되지 않은 경우 파일 표시
                    if (parentFolder.getDriveFolderIsDeleted() == 0) return true;

                    // 파일 삭제 시점이 폴더 삭제 시점보다 이전인 경우 표시
                    return file.getDriveFileDeletedAt() != null
                            && file.getDriveFileDeletedAt().isBefore(parentFolder.getDriveFolderDeletedAt());
                })
                .collect(Collectors.toList());

        // 5. 결과 반환
        Map<String, Object> response = new HashMap<>();
        response.put("folders", trashFolders);
        response.put("files", trashFiles);
        return response;
    }




//    // 휴지통에서 선택된 폴더와 독립 파일 조회
//    public Map<String, Object> MyTrashSelectView(String driveFolderId) {
//        // 선택된 폴더만 조회
//        List<DriveFolderDocument> MyDriveFolders = driveFolderRepository.findWithSelectDeleteFolders(driveFolderId)
//                .stream()
//                .filter(folder -> folder.getDriveParentFolderId() == null) // 상위 폴더만 필터링
//                .collect(Collectors.toList());
//
//        // 선택된 폴더 내 파일 조회
//        List<DriveFileEntity> MyDriveFiles = driveFileRepository.findByDriveFolderIdAndDriveIsDeleted(driveFolderId, 1);
//
//        log.info("오잉 : " + MyDriveFolders);
//        log.info("오이잉 :" + MyDriveFiles);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("folders", MyDriveFolders);  // 상위 폴더만 추가
//        response.put("files", MyDriveFiles);     // 독립 파일만 추가
//
//        return response;
//    }



    //이름바꾸기
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
    public ResponseEntity<?> ToOneMyTrash(String driveFolderNameId, Integer selectedDriveFileId) throws IOException {
        boolean isFileUpdated = false; // 파일 처리 여부
        boolean isFolderUpdated = false; // 폴더 처리 여부

        // 1. 파일 ID 처리
        if (selectedDriveFileId != null && selectedDriveFileId != 0) { // selectedDriveFileId가 0이 아닌지 확인
            Optional<DriveFileEntity> OneFile = driveFileRepository.findById(selectedDriveFileId);
            if (OneFile.isPresent() && OneFile.get().getDriveIsDeleted() == 0) {
                DriveFileEntity file = OneFile.get();
                file.setDriveIsDeleted(1);
                file.setDriveFileDeletedAt(LocalDateTime.now()); // 삭제 시점 설정
                driveFileRepository.save(file);
                isFileUpdated = true;
                log.info("파일 ID {} 휴지통으로 이동 완료. DeletedAt: {}", selectedDriveFileId, file.getDriveFileDeletedAt());
            }
        }

        // 2. 폴더 ID 처리
        if (driveFolderNameId != null) {
            Optional<DriveFolderDocument> driveFolderOpt = driveFolderRepository.finddriveFolderNameById(driveFolderNameId);
            if (driveFolderOpt.isPresent()) {
                DriveFolderDocument rootFolder = driveFolderOpt.get();
                String path = rootFolder.getDriveFolderPath();

                // 2-1. 경로에 포함된 모든 폴더 및 파일 가져오기
                List<DriveFolderDocument> folders = driveFolderRepository.findBydriveFolderPathStartingWith(path);
                List<DriveFileEntity> files = driveFileRepository.findBydriveFilePathStartingWith(path);

                // 2-2. 폴더 상태 변경
                LocalDateTime deleteTimestamp = LocalDateTime.now();
                for (DriveFolderDocument folder : folders) {
                    if (folder.getDriveFolderIsDeleted() == 0) {
                        folder.setDriveFolderIsDeleted(1);
                        folder.setDriveFolderDeletedAt(deleteTimestamp); // 동일한 삭제 시점 설정
                        log.info("폴더 ID {} - '{}' 삭제 완료. DeletedAt: {}", folder.getDriveFolderId(), folder.getDriveFolderName(), folder.getDriveFolderDeletedAt());
                    }
                }
                driveFolderRepository.saveAll(folders);

                // 2-3. 파일 상태 변경
                for (DriveFileEntity file : files) {
                    if (file.getDriveIsDeleted() == 0) {
                        file.setDriveIsDeleted(1);
                        file.setDriveFileDeletedAt(deleteTimestamp); // 동일한 삭제 시점 설정
                        log.info("파일 ID {} - '{}' 삭제 완료. DeletedAt: {}", file.getDriveFileId(), file.getDriveFileSName(), file.getDriveFileDeletedAt());
                    }
                }
                driveFileRepository.saveAll(files);

                isFolderUpdated = true;
                log.info("폴더 ID {} 및 하위 항목 처리 완료. DeletedAt: {}", driveFolderNameId, deleteTimestamp);
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

    //복원원원
    public ResponseEntity<?> ToMyDrive(List<String> driveFolderIdList, List<Integer> selectedDriveFileIds) throws IOException {
        boolean isUpdated = false;

        // 1. 파일 복원 처리
        if (selectedDriveFileIds != null && !selectedDriveFileIds.isEmpty()) {
            for (int driveFileId : selectedDriveFileIds) {
                isUpdated |= restoreFileWithParents(driveFileId);
            }
        }

        // 2. 폴더 복원 처리
        if (driveFolderIdList != null && !driveFolderIdList.isEmpty()) {
            for (String driveFolderId : driveFolderIdList) {
                isUpdated |= restoreFolderWithParents(driveFolderId);
            }
        }

        // 3. 결과 반환
        if (isUpdated) {
            return ResponseEntity.ok("Folders and files restored successfully.");
        } else if ((driveFolderIdList == null || driveFolderIdList.isEmpty()) &&
                (selectedDriveFileIds == null || selectedDriveFileIds.isEmpty())) {
            return ResponseEntity.badRequest().body("No IDs provided.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Drive folder or files not found.");
        }
    }

    // 파일 및 상위 폴더 복원
    private boolean restoreFileWithParents(int fileId) {
        Optional<DriveFileEntity> fileOpt = driveFileRepository.findById(fileId);
        boolean isUpdated = false;

        if (fileOpt.isPresent()) {
            DriveFileEntity file = fileOpt.get();

            // 파일 복원 전 상위 폴더 복원 확인
            String folderId = file.getDriveFolderId();
            if (folderId != null) {
                isUpdated |= restoreFolderWithParents(folderId);
            }

            // 파일 복원
            if (file.getDriveIsDeleted() == 1) {
                file.setDriveIsDeleted(0);
                file.setDriveFileDeletedAt(LocalDateTime.now());
                driveFileRepository.save(file);
                isUpdated = true;
            }
        }

        return isUpdated;
    }

    // 특정 폴더 및 상위 폴더 복원
    private boolean restoreFolderWithParents(String folderId) {
        Optional<DriveFolderDocument> folderOpt = driveFolderRepository.findById(folderId);
        boolean isUpdated = false;

        if (folderOpt.isPresent()) {
            DriveFolderDocument folder = folderOpt.get();

            // 현재 폴더 복원
            if (folder.getDriveFolderIsDeleted() == 1) {
                folder.setDriveFolderIsDeleted(0);
                folder.setDriveFolderDeletedAt(LocalDateTime.now());
                driveFolderRepository.save(folder);
                isUpdated = true;

                // 하위 폴더와 파일 복원 추가
                restoreChildren(folder.getDriveFolderPath());
            }

            // 상위 폴더 복원
            String parentFolderId = folder.getDriveParentFolderId();
            while (parentFolderId != null) {
                Optional<DriveFolderDocument> parentOpt = driveFolderRepository.findById(parentFolderId);
                if (parentOpt.isPresent()) {
                    DriveFolderDocument parentFolder = parentOpt.get();
                    if (parentFolder.getDriveFolderIsDeleted() == 1) {
                        parentFolder.setDriveFolderIsDeleted(0);
                        parentFolder.setDriveFolderDeletedAt(LocalDateTime.now());
                        driveFolderRepository.save(parentFolder);
                        isUpdated = true;
                    }
                    parentFolderId = parentFolder.getDriveParentFolderId();
                } else {
                    break;
                }
            }
        }

        return isUpdated;
    }

    // 특정 경로의 하위 폴더 및 파일 복원
    private void restoreChildren(String folderPath) {
        // 하위 폴더 복원
        List<DriveFolderDocument> subFolders = driveFolderRepository.findBydriveFolderPathStartingWith(folderPath);
        for (DriveFolderDocument subFolder : subFolders) {
            if (subFolder.getDriveFolderIsDeleted() == 1) {
                subFolder.setDriveFolderIsDeleted(0);
                subFolder.setDriveFolderDeletedAt(LocalDateTime.now());
            }
        }
        driveFolderRepository.saveAll(subFolders);

        // 하위 파일 복원
        List<DriveFileEntity> files = driveFileRepository.findBydriveFilePathStartingWith(folderPath);
        for (DriveFileEntity file : files) {
            if (file.getDriveIsDeleted() == 1) {
                file.setDriveIsDeleted(0);
                file.setDriveFileDeletedAt(LocalDateTime.now());
            }
        }
        driveFileRepository.saveAll(files);
    }

//    // 부모 폴더 경로를 재귀적으로 복원
//    private boolean restoreParentFoldersRecursively(String parentPath) {
//        Optional<DriveFolderDocument> parentOpt = driveFolderRepository.findBydriveFolderPath(parentPath);
//        boolean isUpdated = false;
//
//        while (parentOpt.isPresent()) {
//            DriveFolderDocument parentFolder = parentOpt.get();
//
//            if (parentFolder.getDriveFolderIsDeleted() == 1) {
//                parentFolder.setDriveFolderIsDeleted(0);
//                parentFolder.setDriveFolderDeletedAt(LocalDateTime.now());
//                driveFolderRepository.save(parentFolder);
//                isUpdated = true;
//            }
//
//            if (parentFolder.getDriveParentFolderId() != null) {
//                parentOpt = driveFolderRepository.findById(parentFolder.getDriveParentFolderId());
//            } else {
//                break;
//            }
//        }
//
//        return isUpdated;
//    }


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
    //폴더 즐겨찾기 (단독파일 이동 차후 추가)
    public DriveIsStaredResponseDTO DriveIsStared(DriveIsStarredDTO driveIsStarredDTO) throws IOException {
        log.info("DriveIsStared : " + driveIsStarredDTO);
        Optional<DriveIsStared> isStaredopt= driveIsStaredRepository.findByUserIdAndDriveFolderIdAndDriveFileId(driveIsStarredDTO.getUserId(), driveIsStarredDTO.getDriveFolderId(), driveIsStarredDTO.getDriveFileId());
        log.info("메롱메롱 : " + isStaredopt);
        DriveIsStared savedEntity;

        if (isStaredopt.isPresent()) {
            // 기존 데이터 업데이트
            DriveIsStared updateIsStar = isStaredopt.get();
            updateIsStar.setStared(!updateIsStar.isStared()); // 상태 토글
            savedEntity = driveIsStaredRepository.save(updateIsStar);
        } else {
            // 새 데이터 생성 및 저장
            DriveIsStared newEntity = DriveIsStared.builder()
                    .driveFolderId(driveIsStarredDTO.getDriveFolderId())
                    .userId(driveIsStarredDTO.getUserId())
                    .driveFileId(driveIsStarredDTO.getDriveFileId())
                    .isStared(true)
                    .build();
            savedEntity = driveIsStaredRepository.save(newEntity);
        }

        // 저장된 엔티티를 DTO로 변환하여 반환
        return DriveIsStaredResponseDTO.builder()
                .driveFolderId(savedEntity.getDriveFolderId())
                .userId(savedEntity.getUserId())
                .isStared(savedEntity.isStared())
                .driveFileId(savedEntity.getDriveFileId())
                .build();
    }
    // 드라이브 폴더 이동
    public ResponseEntity<?> DriveMoveToFolder(DriveMoveRequestDTO driveMoveRequestDTO) throws IOException {

        String userId = driveMoveRequestDTO.getUserId();

        // 이동될 폴더 ID와 이동할 폴더 ID 가져오기
        String originFolderId = driveMoveRequestDTO.getDriveFolderId(); // 이동될 폴더
        String targetFolderId = driveMoveRequestDTO.getSelectDriveFolderId(); // 이동할 폴더
        Integer originFileId = driveMoveRequestDTO.getDriveFileId(); // 이동될 파일 (null 가능)

        // 예외 처리: 이동 대상이 없는 경우
        if (originFileId == null && originFolderId == null) {
            throw new IllegalArgumentException("이동될 파일 또는 폴더 ID가 필요합니다.");
        }

        log.info("이까지는 들어오긴 하나? originFileId: {}", originFileId);

        // 이동할 폴더 조회
        Optional<DriveFolderDocument> targetFolderOpt = driveFolderRepository.findById(targetFolderId);
        if (!targetFolderOpt.isPresent()) {
            throw new IllegalArgumentException("이동할 폴더를 찾을 수 없습니다: " + targetFolderId);
        }
        DriveFolderDocument targetFolder = targetFolderOpt.get();
        String targetPath = targetFolder.getDriveFolderPath();

        // 단일 파일 이동
        if (originFileId != 0) {
            Optional<DriveFileEntity> fileOpt = driveFileRepository.findById(originFileId);
            if (!fileOpt.isPresent()) {
                throw new IllegalArgumentException("이동될 파일을 찾을 수 없습니다: " + originFileId);
            }
            DriveFileEntity file = fileOpt.get();

            log.info("단일 파일 이동 시작: {}", file);

            // 물리적 경로 이동
            movePhysicalPaths(Collections.emptyList(), Collections.emptyList(), targetPath, userId, Optional.of(file));

            // 데이터베이스 경로 업데이트
            updateDatabasePaths(Collections.emptyList(), Collections.emptyList(), targetPath, userId, targetFolderId, null, Optional.of(file));
            return ResponseEntity.ok("단일 파일 이동 완료.");
        }

        // 폴더 이동
        if (originFolderId != null) {
            Optional<DriveFolderDocument> sourceFolderOpt = driveFolderRepository.findById(originFolderId);
            if (!sourceFolderOpt.isPresent()) {
                throw new IllegalArgumentException("이동될 폴더를 찾을 수 없습니다: " + originFolderId);
            }
            DriveFolderDocument originFolder = sourceFolderOpt.get();
            String originPath = originFolder.getDriveFolderPath();

            String userBasePath = "/uploads/drive/" + userId + "/";
            String relativeFolderPath = targetPath.replaceFirst(userBasePath, ""); // UUID만
            String relativeFolderPath2 = originPath.replaceFirst(userBasePath, "");

            log.info("이거 UUID만 잇어? " + relativeFolderPath);
            log.info("이것도? UUID 만 있어? " + relativeFolderPath2);

            if (relativeFolderPath.contains(relativeFolderPath2)) {
                throw new IOException("하위 폴더로 이동할 수 없습니다");
            }

            // 이동하려는 폴더 안의 모든 하위 폴더와 파일 가져오기
            List<DriveFolderDocument> folders = driveFolderRepository.findBydriveFolderPathStartingWith(originPath);
            List<DriveFileEntity> files = driveFileRepository.findBydriveFilePathStartingWith(originPath);

            log.info("폴더 이동 시작: folders={}, files={}", folders, files);

            // 물리적 경로 이동
            movePhysicalPaths(folders, files, targetPath, userId, Optional.empty());

            // 데이터베이스 경로 업데이트
            updateDatabasePaths(folders, files, targetPath, userId, targetFolderId, originFolderId, Optional.empty());
            return ResponseEntity.ok("단일 파일 이동 완료.");
        }
        return null;
    }
    //물리적 경로 이동
    private void movePhysicalPaths(
            List<DriveFolderDocument> folders,
            List<DriveFileEntity> files,
            String targetPath,
            String userId,
            Optional<DriveFileEntity> onefile
    ) throws IOException {

        String userDir = System.getProperty("user.dir");
        String userBasePath = "/uploads/drive/" + userId + "/";
        log.info("여기는 찍히니? : " + userDir);
        if (onefile.isPresent()) {
            DriveFileEntity file = onefile.get();

            Path originFilePath = Paths.get(userDir + "/" + file.getDriveFilePath());
            Path targetParentPath = Paths.get(userDir + "/" + targetPath);
            Path targetFilePath = targetParentPath.resolve(originFilePath.getFileName());

            // 로그 출력
            log.info("파일 원본 경로: {}", originFilePath);
            log.info("파일 대상 경로: {}", targetFilePath);

            // 부모 폴더 생성 (대상 경로의 부모 폴더가 없으면 생성)
            if (!Files.exists(targetParentPath)) {
                Files.createDirectories(targetParentPath);
                log.info("대상 경로의 부모 폴더 생성: {}", targetParentPath);
            }

            // 파일 이동
            try {
                Files.move(originFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
                log.info("한개파일 이동 성공: {} -> {}", originFilePath, targetFilePath);
            } catch (IOException e) {
                log.error("한개파일 이동 중 오류 발생: {} -> {}", originFilePath, targetFilePath, e);
            }

            // 이동 후 상태 확인
            if (!Files.exists(originFilePath)) {
                log.info("한개파일 이동되었음 ");
            } else {
                log.info("한개파일 이동안됨.. ");
            }

            // 파일 이동만 수행했으므로 함수 종료
            return;
        }
        for (DriveFolderDocument folder : folders) {

            Path originFolderPath = Paths.get(userDir + "/" + folder.getDriveFolderPath());

            // 대상 경로 계산: 대상 경로의 하위로 이동
            Path targetParentPath = Paths.get(userDir + "/" + targetPath);
            Path targetFolderPath = targetParentPath.resolve(originFolderPath.getFileName());

            // 로그 출력
            log.info("원본 경로: {}", originFolderPath);
            log.info("대상 경로: {}", targetFolderPath);


            // 부모 폴더 생성 (대상 경로의 부모 폴더가 없으면 생성)
            if (!Files.exists(targetParentPath)) {
                Files.createDirectories(targetParentPath);
                log.info("대상 폴더의 부모 경로 생성: {}", targetParentPath);
            }

            // 폴더 이동
            try {
                Files.move(originFolderPath, targetFolderPath, StandardCopyOption.REPLACE_EXISTING);
                log.info("폴더 이동 성공: {} -> {}", originFolderPath, targetFolderPath);
            }catch (IOException e) {
                log.error("파일 이동 중 오류 발생: {} -> {}", originFolderPath, targetFolderPath, e);
            }
            if(!Files.exists(originFolderPath)){
                log.info("이동되었음 ");
            }else {
                log.info("이동안됨.. ");
            }
        }

        for (DriveFileEntity file : files) {
//            String relativeFilerPath = file.getDriveFilePath().replaceFirst(userBasePath, "");//UUID만있음
//            Path sourceFilePath = Paths.get(userDir + "/" + file.getDriveFilePath());
//            Path targetFilePath = Paths.get(userDir + "/" + targetPath + "/" +relativeFilerPath);

            Path originFilePath = Paths.get(userDir + "/" + file.getDriveFilePath());

            // 대상 경로 계산: 대상 경로의 하위로 이동
            Path targetParentPath = Paths.get(userDir + "/" + targetPath);
            Path targetFilePath = targetParentPath.resolve(originFilePath.getFileName());

            // 로그 출력
            log.info("파일 원본 경로: {}", originFilePath);
            log.info("파일 대상 경로: {}", targetFilePath);

            // 폴더 이동
            try {
                Files.move(originFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
                log.info("파일 이동 성공: {} -> {}", originFilePath, targetFilePath);
            }catch (IOException e) {
                log.error("파일 이동 중 오류 발생: {} -> {}", originFilePath, targetFilePath, e);
            }
            if(!Files.exists(originFilePath)){
                log.info("파일 이동되었음 ");
            }else {
                log.info("파일 이동안됨.. ");
            }
        }
    }
    // 데이터베이스 업데이트 치기
    private void updateDatabasePaths(
            List<DriveFolderDocument> folders,
            List<DriveFileEntity> files,
            String targetPath,
            String userId,
            String targetFolderId,
            String originFolderId,
            Optional<DriveFileEntity> oneFile
    ) {
        String userBasePath = "/uploads/drive/" + userId + "/";

        // 1. 단일 파일 처리
        if (oneFile.isPresent()) {
            DriveFileEntity singleFile = oneFile.get();
            singleFile.setDriveFolderId(targetFolderId);

            // 경로 갱신
            String relativeFilePath = singleFile.getDriveFilePath().replaceFirst(".*/", "");
            String updatedPath = targetPath + "/" + relativeFilePath;
            singleFile.setDriveFilePath(updatedPath);
            // 저장
            driveFileRepository.save(singleFile);
            log.info("단독 파일의 데이터베이스 경로 업데이트 완료: {}", singleFile);
            return; // 단일 파일 처리 후 종료
        }

        // 2. 이동하려는 최상위 폴더 필터링
        List<DriveFolderDocument> rootFolders;
        if (originFolderId == null) {
            // 최상위 폴더의 경우
            rootFolders = folders.stream()
                    .filter(folder -> folder.getDriveParentFolderId() == null)
                    .collect(Collectors.toList());
        } else {
            // 특정 폴더의 하위 구조를 이동하는 경우
            rootFolders = folders.stream()
                    .filter(folder -> folder.getDriveFolderId().equals(originFolderId))
                    .collect(Collectors.toList());
        }

        // 3. 최상위 폴더부터 재귀적으로 갱신 시작
        for (DriveFolderDocument rootFolder : rootFolders) {
            rootFolder.setDriveParentFolderId(targetFolderId); // 부모 ID 갱신
            recursivelyUpdatePaths(rootFolder, targetPath);
        }

        log.info("데이터베이스 경로 업데이트 완료");
    }

    // 재귀적으로 하위 폴더와 파일의 경로를 갱신
    private void recursivelyUpdatePaths(DriveFolderDocument parentFolder, String newParentPath) {
        // 상위 폴더의 새 경로 갱신
        log.info("parentFolder 아이디 새로운 것 들 들어와야 돼 : " + parentFolder);
        String updatedParentPath = newParentPath + "/" + parentFolder.getDriveFolderPath().replaceFirst(".*/", ""); // 기존 경로의 마지막 부분 유지
        log.info("새 경로 갱신 : " + updatedParentPath);
        parentFolder.setDriveFolderPath(updatedParentPath);
        log.info("현재 paretnFolder는? : " + parentFolder);

        driveFolderRepository.save(parentFolder);

        // 하위 폴더 갱신
        List<DriveFolderDocument> childFolders = driveFolderRepository.findWithSelectFolders(parentFolder.getDriveFolderId());
        log.info("자식 뭐로 나오나 ? : " + childFolders);
        for (DriveFolderDocument childFolder : childFolders) {
            childFolder.setDriveParentFolderId(parentFolder.getDriveFolderId()); // 부모 ID 갱신
            log.info("자식 부모 아이디 재 생성 : " + childFolder);
            recursivelyUpdatePaths(childFolder, updatedParentPath); // 재귀 호출
        }

        // 하위 파일 갱신
        List<DriveFileEntity> childFiles = driveFileRepository.findByDriveFolderId(parentFolder.getDriveFolderId());
        for (DriveFileEntity file : childFiles) {
            String updatedFilePath = updatedParentPath + "/" + file.getDriveFilePath().replaceFirst(".*/", ""); // 기존 경로의 마지막 부분 유지
            file.setDriveFilePath(updatedFilePath);
        }
        driveFileRepository.saveAll(childFiles); // 파일 저장
    }


}
