package BackAnt.controller.drive;

import BackAnt.dto.drive.DriveNewFileInsertDTO;
import BackAnt.dto.drive.DriveNewFolderInsertDTO;
import BackAnt.dto.drive.MyDriveViewDTO;
import BackAnt.service.DriveFileService;
import BackAnt.service.DriveFolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/folder/myDriveView")
    public ResponseEntity<?> MydriveView(){
        Map<String, Object> Mydrive = driveFolderService.MyDriveView();
        log.info("마이드라이브여야해 : " + Mydrive);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Mydrive);

    }

    @GetMapping("/folder/myDriveSelectView/{driveFolderId}")
    public ResponseEntity<?> MyDriveSelectView(@PathVariable String driveFolderId){
        log.info("asdfasdf : " + driveFolderId);
        List<MyDriveViewDTO> myDriveDTOs = driveFolderService.MyDriveSelectView(driveFolderId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(myDriveDTOs);
    }

    @PostMapping("/files/insert")
    public ResponseEntity<?> filesInsert(DriveNewFileInsertDTO DriveNewFileInsertDTO){
        log.info("야옹야옹야옹양옹헝 : " + DriveNewFileInsertDTO);
        List<DriveNewFileInsertDTO> driveFile = driveFileService.fileInsert(DriveNewFileInsertDTO);
        log.info("양양양 :" + driveFile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(driveFile);
    }
}
