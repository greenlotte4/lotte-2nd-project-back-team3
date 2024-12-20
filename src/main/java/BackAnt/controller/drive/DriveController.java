package BackAnt.controller.drive;

import BackAnt.dto.drive.*;
import BackAnt.service.DriveFileService;
import BackAnt.service.DriveFolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/drive")
public class DriveController {

    private final DriveFolderService driveFolderService;
    private final DriveFileService driveFileService;

    @PostMapping("/folder/insert")
    public ResponseEntity<?> folderInsert(@RequestBody DriveNewFolderInsertDTO driveNewFolderInsertDTO){
        log.info(driveNewFolderInsertDTO);
        DriveNewFolderInsertDTO folderDTO = driveFolderService.FolderNewInsert(driveNewFolderInsertDTO);
        log.info("aaaaaaaaa : " + folderDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(folderDTO);
    }
//마이드라이브 전체보기
    @GetMapping("/folder/myDriveView/{uid}")
    public ResponseEntity<?> MydriveView(@PathVariable String uid){
        Map<String, Object> Mydrive = driveFolderService.MyDriveView(uid);
        log.info("마이드라이브여야해 : " + Mydrive);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Mydrive);

    }
//마이드라이브선택보기
    @GetMapping("/folder/myDriveSelectView/{driveFolderId}")
    public ResponseEntity<?> MyDriveSelectView(@PathVariable String driveFolderId){
        log.info("asdfasdf : " + driveFolderId);
        Map<String, Object> MySelectDrive = driveFolderService.MyDriveSelectView(driveFolderId);
//        List<MyDriveViewDTO> myDriveDTOs = driveFolderService.MyDriveSelectView(driveFolderId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MySelectDrive);
    }
//휴지통전체보기
    @GetMapping("/folder/myTrashView/{uid}")
    public ResponseEntity<?> MyTrashView(@PathVariable String uid){
        Map<String, Object> MyTrash = driveFolderService.MyTrashView(uid);
        log.info("휴지통이어야해 : " + MyTrash);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MyTrash);

    }
//    //휴지통선택보기
//    @GetMapping("/folder/myTrashSelectView/{driveFolderId}")
//    public ResponseEntity<?> MyTrashSelectView(@PathVariable String driveFolderId){
//        log.info("여기로 온건 맞아? : " + driveFolderId);
//        Map<String, Object> MySelectTrash = driveFolderService.MyTrashSelectView(driveFolderId);
//        log.info("와랄랄랄랄랄 : " + MySelectTrash);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(MySelectTrash);
//    }

    @PostMapping("/files/insert")
    public ResponseEntity<?> filesInsert(DriveNewFileInsertDTO DriveNewFileInsertDTO){
        log.info("야옹야옹야옹양옹헝 : " + DriveNewFileInsertDTO);
        List<DriveNewFileInsertDTO> driveFile = driveFileService.fileInsert(DriveNewFileInsertDTO);
        log.info("양양양 :" + driveFile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(driveFile);
    }
//파일다운로드
    @GetMapping("/files/MyDriveFileDownload")
    public ResponseEntity<?> MyDriveFileDownload(@RequestParam int driveFileId){
        log.info(driveFileService.MyDriveFileDownload(driveFileId));
        return driveFileService.MyDriveFileDownload(driveFileId);



    }
@PostMapping("/folder/name")
    public ResponseEntity<?> DriveFolderFind(@RequestBody DriveFolderNameDTO driveFolderNameDTO){
        log.info("머라머라머라 : " + driveFolderNameDTO);
    DriveNewFolderInsertDTO FolderNameDto = driveFolderService.DriveFolderFind(driveFolderNameDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(FolderNameDto);
}
//단일 휴지통
@GetMapping("/folder/toOneTrash/{driveFolderNameId}/{selectedDriveFileId}")
public ResponseEntity<?> DriveFolderTrash(    @PathVariable(required = false) String driveFolderNameId,
                                              @PathVariable(required = false) Integer selectedDriveFileId) throws IOException {
    log.info("고양이야옹 : " + driveFolderNameId);

    return driveFolderService.ToOneMyTrash(driveFolderNameId,selectedDriveFileId);

    }
    //복원
    @PostMapping("/folder/toDrive")
    public ResponseEntity<?> TrashFolderDrive(@RequestBody DriveFolderFileToTrashDTO driveFolderFileToTrashDTO) throws IOException {
        log.info("마요야 밥은 : " + driveFolderFileToTrashDTO.getDriveFolderId());
        log.info("마요야 먹었어? : " + driveFolderFileToTrashDTO.getSelectedDriveFileIds());
        return driveFolderService.ToMyDrive(driveFolderFileToTrashDTO.getDriveFolderId(), driveFolderFileToTrashDTO.getSelectedDriveFileIds());
    }

    //휴지통으로
    @PostMapping("/folder/toTrash")
    public ResponseEntity<?> DriveFolderTrash(@RequestBody DriveFolderFileToTrashDTO driveFolderFileToTrashDTO) throws IOException {
        log.info("마요야 보고시펑 : " + driveFolderFileToTrashDTO.getDriveFolderId());
        log.info("마요야 보고시펑 : " + driveFolderFileToTrashDTO.getSelectedDriveFileIds());
        return driveFolderService.ToMyTrash(driveFolderFileToTrashDTO.getDriveFolderId(), driveFolderFileToTrashDTO.getSelectedDriveFileIds());
    }
    //파일 총 사이즈 구하기
    @GetMapping("/files/totalSize/{uid}")
    public ResponseEntity<?> totalSize(@PathVariable String uid){
        DriveFileStorageDTO storageDTO = driveFileService.SelectDriveTotalSize(uid);
        log.info("머야 총용량이다 : " + storageDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storageDTO);

    }
}
