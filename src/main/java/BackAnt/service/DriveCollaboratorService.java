package BackAnt.service;


import BackAnt.document.page.drive.DriveFolderDocument;
import BackAnt.dto.drive.DriveCollaboratorDTO;
import BackAnt.dto.drive.DriveShareAllViewDTO;
import BackAnt.dto.drive.DriveShareSelectFilesViewDTO;
import BackAnt.dto.drive.DriveShareSelectViewDTO;
import BackAnt.entity.drive.DriveCollaborator;
import BackAnt.entity.drive.DriveFileEntity;
import BackAnt.entity.User;
import BackAnt.entity.drive.DriveIsStared;
import BackAnt.repository.drive.DriveFileRepository;
import BackAnt.repository.drive.DriveCollaboratorRepository;
import BackAnt.repository.drive.DriveIsStaredRepository;
import BackAnt.repository.mongoDB.drive.DriveFolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class DriveCollaboratorService {

    private final DriveFolderRepository driveFolderRepository;
    private final DriveCollaboratorRepository driveCollaboratorRepository;
    private final DriveIsStaredRepository driveIsStaredRepository;
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
                .isSharePoint(true)
                .build();
        driveCollaboratorRepository.save(driveCollaborator);
        log.info("메ㅑㅁ모먀모 : " + driveCollaborator);

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
                    folder.setDriveFolderSharedAt(LocalDateTime.now());
                }

            }
            driveFolderRepository.saveAll(folders);
            for(DriveFileEntity file : files) {
                if(file.getDriveIsDeleted() == 0){
                    file.setDriveShareType(1);
                    file.setDriveFileSharedAt(LocalDateTime.now());
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


    //공유 드라이브 전체 조회(파일x)
    public Map<String, Object> shareDriveView(Long userId,String uid){
        List<DriveIsStared> MyDriveIsStared = driveIsStaredRepository.findByUserId(uid);
        log.info("좋아요한것 : "+MyDriveIsStared);

        // 좋아요 데이터를 Map으로 변환 (driveFolderId -> isStared)
        Map<String, Boolean> staredMap = MyDriveIsStared.stream()
                .filter(item -> item.getDriveFolderId() != null) // 폴더 데이터만 포함
                .collect(Collectors.toMap(
                        DriveIsStared::getDriveFolderId,
                        DriveIsStared::isStared
                ));

        List<DriveShareAllViewDTO> driveShareAllviewsDTO = new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        List<String> driveFolderIds = driveCollaboratorRepository.findDriveFolderIdsByConditions(userId);
        log.info("공유드라이브 조회하기... : " + driveFolderIds);
        if(driveFolderIds.size() >0 ){
            log.info("사이즈 0 이상");
            for(String DriveFolderId : driveFolderIds) {
                Optional<DriveFolderDocument> driveShareFolders = driveFolderRepository.findShareWithFolderId(DriveFolderId);
                if (driveShareFolders.isPresent()) {
                    DriveFolderDocument driveShareFolder = driveShareFolders.get();
                    DriveShareAllViewDTO allviewDTO = DriveShareAllViewDTO.builder()
                            .driveFolderId(DriveFolderId)
                            .driveFolderName(driveShareFolder.getDriveFolderName())
                            .driveParentFolderId(driveShareFolder.getDriveParentFolderId())
                            .driveFolderSize(driveShareFolder.getDriveFolderSize())
                            .driveFolderCreatedAt(driveShareFolder.getDriveFolderCreatedAt())
                            .driveFolderSharedAt(driveShareFolder.getDriveFolderSharedAt())
                            .driveFolderShareType(driveShareFolder.getDriveFolderShareType())
                            .driveFolderMaker(driveShareFolder.getDriveFolderMaker())
                            .driveFolderIsStared(staredMap.getOrDefault(driveShareFolder.getDriveFolderId(), false))
                            .build();

                    driveShareAllviewsDTO.add(allviewDTO);
                }
            }
            log.info("ㅠㅠㅠ메메메,, : " + driveShareAllviewsDTO);

            response.put("folders", driveShareAllviewsDTO);  // MyDriveFolders를 "folders"라는 키로 추가
            log.info("힝 ,,, : " + response);
        }

        return response;

    }

    //공유 드라이브 선택조회 (파일 차후 추가)
    public Map<String, Object> ShareDriveSelectView(String driveFolderId, String uid){
        List<DriveIsStared> MyDriveIsStared = driveIsStaredRepository.findByUserId(uid);
        log.info("좋아요한것 : "+MyDriveIsStared);

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

        List<DriveShareSelectViewDTO> selectViewsDTO = new ArrayList<>();
        List<DriveShareSelectFilesViewDTO> selectFilesDTO = new ArrayList<>();
        log.info("gbgbgbgbgb : " + driveFolderId);

        List<DriveFolderDocument> shareDriveFolders = driveFolderRepository.findWithSelectShareFolders(driveFolderId);
        log.info("먀먀먀 : " + shareDriveFolders);
        List<DriveFileEntity> fileEntites = driveFileRepository.findByDriveFolderIdAndDriveIsDeletedAndDriveShareType(driveFolderId,0,1);
        log.info("묘묘묘묘 : " + fileEntites);


        for(DriveFolderDocument ShareFolder : shareDriveFolders){
            DriveShareSelectViewDTO selectViewDTO = DriveShareSelectViewDTO.builder()
                    .driveFolderId(ShareFolder.getDriveFolderId())
                    .driveFolderName(ShareFolder.getDriveFolderName())
                    .driveParentFolderId(ShareFolder.getDriveParentFolderId())
                    .driveFolderSize(ShareFolder.getDriveFolderSize())
                    .driveFolderSharedAt(ShareFolder.getDriveFolderCreatedAt())
                    .driveFolderUpdatedAt(ShareFolder.getDriveFolderUpdatedAt())
                    .driveFolderMaker(ShareFolder.getDriveFolderMaker())
                    .driveFolderShareType(ShareFolder.getDriveFolderShareType())
                    .driveFolderIsStared(staredMap.getOrDefault(ShareFolder.getDriveFolderId(), false)) // 좋아요 상태 (없으면 false)
                    .build();

            selectViewsDTO.add(selectViewDTO);
        }

        for(DriveFileEntity ShareFile : fileEntites){
            DriveShareSelectFilesViewDTO selectFileViewDTO = DriveShareSelectFilesViewDTO.builder()
                    .driveFileId(ShareFile.getDriveFileId())
                    .driveFileSName(ShareFile.getDriveFileSName())
                    .driveFileSize(ShareFile.getDriveFileSize())
                    .driveFolderId(ShareFile.getDriveFolderId())
                    .driveFileMaker(ShareFile.getDriveFileMaker())
                    .driveFileCreatedAt(ShareFile.getDriveFileCreatedAt())
                    .driveFileSharedAt(ShareFile.getDriveFileSharedAt())
                    .driveShareType(ShareFile.getDriveShareType())
                    .driveIsStarted(staredFileMap.getOrDefault(ShareFile.getDriveFileId(), false))
                    .build();

            selectFilesDTO.add(selectFileViewDTO);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("folders", selectViewsDTO);  // MyDriveFolders를 "folders"라는 키로 추가
        log.info("ㄴㄷㄴㄷ : " + selectViewsDTO);
        log.info("ㄷㄷㄷㄷ :" + selectFilesDTO);
        response.put("files", selectFilesDTO);
        log.info("힝 ,,, : " + response);
        return response;
    }


}
