package BackAnt.service;


import BackAnt.document.page.drive.DriveFolderDocument;
import BackAnt.dto.UserDTO;
import BackAnt.dto.drive.DriveCollaboratorDTO;
import BackAnt.dto.page.PageCollaboratorDTO;
import BackAnt.entity.DriveCollaborator;
import BackAnt.entity.DriveFileEntity;
import BackAnt.entity.User;
import BackAnt.entity.page.PageCollaborator;
import BackAnt.entity.project.ProjectCollaborator;
import BackAnt.repository.DriveFileRepository;
import BackAnt.repository.drive.DriveCollaboratorRepository;
import BackAnt.repository.mongoDB.drive.DriveFolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class DriveCollaboratorService {

    private final DriveFolderRepository driveFolderRepository;
    private final DriveCollaboratorRepository driveCollaboratorRepository;
    private final DriveFileRepository driveFileRepository;
    private final ModelMapper modelMapper;


    // 폴더별 협업자 조회
    public List<DriveCollaboratorDTO> getUsersByDriveId(String DriveFolderId) {

        log.info("projectId : " + DriveFolderId);
        String driveFolderId = new ObjectId(DriveFolderId).toString();
        log.info("메메메메메메 : " + driveFolderId);
        List<DriveCollaborator> driveCollaborators = driveCollaboratorRepository.findByDriveFolderIdWithQuery(driveFolderId);
        log.info("users : " + driveCollaborators);

        return driveCollaborators.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Entity를 DTO로 변환
    private DriveCollaboratorDTO convertToDTO(DriveCollaborator collaborator) {
        return DriveCollaboratorDTO.builder()
                .DriveFolderShareId(collaborator.getDriveFolderShareId())
                .DriveFolderId(collaborator.getDriveFolderId())
                .user_id(collaborator.getUser().getId())
                .uidImage(collaborator.getUser().getProfileImageUrl())
                .DriveFolderShareAt(collaborator.getDriveFolderShareAt())
                .isOwner(collaborator.isOwner())
                .DriveShareType(collaborator.getDriveShareType())
                .build();
    }

    //폴더별 협업자 추가
    public void addDriveCollaborator(String driveFolderNameId, User user, int type) {

        DriveCollaborator driveCollaborator = DriveCollaborator.builder()
                .driveFolderId(driveFolderNameId)
                .user(user)
                .isOwner(false)
                .driveShareType(type)
                .driveFolderShareAt(LocalDateTime.now())
                .build();
        driveCollaboratorRepository.save(driveCollaborator);

        Optional<DriveFolderDocument> driveFolderOpt = driveFolderRepository.finddriveFolderNameById(driveFolderNameId);
        if (driveFolderOpt.isPresent()) {
            DriveFolderDocument rootFolder = driveFolderOpt.get();
            String path = rootFolder.getDriveFolderPath();

            // 2-1. 경로에 포함된 모든 폴더 및 파일 가져오기
            List<DriveFolderDocument> folders = driveFolderRepository.findBydriveFolderPathStartingWith(path);
            List<DriveFileEntity> files = driveFileRepository.findBydriveFilePathStartingWith(path);

            for(DriveFolderDocument folder : folders) {
                if(folder.getDriveFolderIsDeleted() == 0){
                    folder.setDriveFolderShareType(1);
                }

            }
            driveFolderRepository.saveAll(folders);
            for(DriveFileEntity file : files) {
                if(file.getDriveIsDeleted() == 0){
                    file.setDriveShareType(1);
                }
            }
            driveFileRepository.saveAll(files);
        }




    }

    // 폴더 id와 사용자 id를 기준으로 협업자 삭제
    public void deleteCollaborator(String DriveFolderId, Long userId) {
        DriveCollaborator collaborator = driveCollaboratorRepository
                .findByDriveFolderIdAndUserId(DriveFolderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 협업자가 존재하지 않습니다."));
        log.info("협업자삭제하고싶다 : " + collaborator);
        driveCollaboratorRepository.delete(collaborator);
    }
}
