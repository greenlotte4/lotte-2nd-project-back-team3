package BackAnt.controller.drive;

import BackAnt.dto.drive.DriveCollaboratorDTO;
import BackAnt.entity.User;
import BackAnt.entity.project.Project;
import BackAnt.repository.UserRepository;
import BackAnt.service.DriveCollaboratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/drive/collaborator")
public class DriveCollaboratorController {

    private final DriveCollaboratorService driveCollaboratorService;
    private final UserRepository userRepository;

    // 폴더별 등록된 협업자 조회
    @GetMapping("/select/{driveFolderNameId}")
    public ResponseEntity<List<DriveCollaboratorDTO>> selectDriveCollaborators(@PathVariable String driveFolderNameId) {
        log.info("projectId: " + driveFolderNameId);

        List<DriveCollaboratorDTO> collaborators = driveCollaboratorService.getUsersByDriveId(driveFolderNameId);
        log.info("collaborators: " + collaborators);

        return ResponseEntity.ok(collaborators);

    }
    @PostMapping("/insert/{driveFolderNameId}")
    public ResponseEntity<?> insertDriveCollaborator(@PathVariable String driveFolderNameId, @RequestBody List<Long> userIds){

        log.info("projectId: " + driveFolderNameId);
        log.info("userIds: " + userIds);




        // 프론트에서 선택된 협업자 추가
        for(Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            driveCollaboratorService.addDriveCollaborator(driveFolderNameId, user, 1);
        }

        return ResponseEntity.ok("협업자가 성공적으로 추가되었습니다.");
    }
    @DeleteMapping("/delete/{driveFolderNameId}/{userId}")
    public ResponseEntity<?> deleteDriveCollaborator(            @PathVariable("driveFolderNameId") String driveFolderNameId,
                                                                 @PathVariable("userId") Long userId){
        driveCollaboratorService.deleteCollaborator(driveFolderNameId, userId);
        log.info("여기러 정보 왜 안와? 이상하네 : " + driveFolderNameId);
        log.info("여기러 정보 왜 안와? 이상하네 : " + userId);

        return ResponseEntity.ok("협업자가 성공적으로 삭제되었습니다.");
    }
    //공유드라이브 전체보기
    @GetMapping("/ShareDriveView/{userId}/{uid}")
    public ResponseEntity<?> ShareDriveView(@PathVariable Long userId, @PathVariable String uid){

       Map<String, Object> ShareDrive = driveCollaboratorService.shareDriveView(userId,uid);
       log.info("공유드라이브여야해,,,: "+ShareDrive);
       return ResponseEntity.status(HttpStatus.CREATED)
                .body(ShareDrive);
    }
    //공유드라이브 선택보기
    @GetMapping("/ShareDriveSelectView/{driveFolderId}/{uid}")
    public ResponseEntity<?> ShareDriveSelectView(@PathVariable String driveFolderId, @PathVariable String uid){

        Map<String, Object> ShareDrive = driveCollaboratorService.ShareDriveSelectView(driveFolderId, uid);
        log.info("선택된 공유 드라이브여야해,,,: "+ ShareDrive);
        log.info("냐냐냐뇽뇽냐냐뇨욘요 : " + driveFolderId);
        log.info("냐냐냐뇽뇽냐냐뇨욘요?? : " + uid);
        log.info("마마마마마마난 : " + ShareDrive);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ShareDrive);
    }
}

