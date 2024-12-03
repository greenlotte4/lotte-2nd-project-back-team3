package BackAnt.controller.drive;

import BackAnt.dto.drive.DriveNewFolderInsertDTO;
import BackAnt.dto.drive.MyDriveViewDTO;
import BackAnt.service.DriveFolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/drive")
public class DriveController {

    private final DriveFolderService driveFolderService;

    @PostMapping("/folder/insert")
    public ResponseEntity<?> folderInsert(@RequestBody DriveNewFolderInsertDTO driveNewFolderInsertDTO){
        log.info(driveNewFolderInsertDTO);
        DriveNewFolderInsertDTO folderDTO = driveFolderService.FolderNewInsert(driveNewFolderInsertDTO);
        log.info("aaaaaaaaa" + folderDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(folderDTO);
    }

    @GetMapping("/folder/myDriveView")
    public ResponseEntity<?> MydriveView(){
        List<MyDriveViewDTO> myDriveDTOs = driveFolderService.MyDriveView();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(myDriveDTOs);
    }

    @GetMapping("/folder/myDriveSelectView/{driveFolderId}")
    public ResponseEntity<?> MyDriveSelectView(@PathVariable String driveFolderId){
        log.info("asdfasdf : " + driveFolderId);
        List<MyDriveViewDTO> myDriveDTOs = driveFolderService.MyDriveSelectView(driveFolderId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(myDriveDTOs);
    }
}
