package BackAnt.service;

import BackAnt.document.page.drive.DriveFolderDocument;
import BackAnt.dto.drive.DriveNewFolderInsertDTO;
import BackAnt.dto.drive.MyDriveViewDTO;
import BackAnt.repository.mongoDB.drive.DriveFolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class DriveFolderService {

    private final DriveFolderRepository driveFolderRepository;
    private final ModelMapper modelMapper;

    public DriveNewFolderInsertDTO FolderNewInsert(DriveNewFolderInsertDTO driveNewFolderInsertDTO) { // 조건걸고 수정해야됨


        String parentFolderPath = "/documents";

        if(driveNewFolderInsertDTO.getDriveParentFolderId() != null) {
           driveFolderRepository.findById(driveNewFolderInsertDTO.getDriveFolderId());
        }else {
            String newFolderPath = parentFolderPath + "/" + driveNewFolderInsertDTO.getDriveFolderName();
            DriveFolderDocument newFolder = DriveFolderDocument.builder()
                    .driveFolderName(driveNewFolderInsertDTO.getDriveFolderName())
                    .driveFolderPath(newFolderPath)
                    .driveFolderCreatedAt(LocalDateTime.now())
                    .driveFolderUpdatedAt(LocalDateTime.now())
                    .build();

            return modelMapper.map(driveFolderRepository.save(newFolder), DriveNewFolderInsertDTO.class);

        }
    return null;
    }
    public List<MyDriveViewDTO> MyDriveView() {
        List<DriveFolderDocument> MyDriveFolders = driveFolderRepository.findAllWithFolders();

        List<MyDriveViewDTO> myDriveViewDTOList = MyDriveFolders.stream()
                .map(folder -> modelMapper.map(folder, MyDriveViewDTO.class))
                .collect(Collectors.toList());


        return myDriveViewDTOList;
    }

    public List<MyDriveViewDTO> MyDriveSelectView(String driveFolderId){
        List<DriveFolderDocument> MyDriveFolders = driveFolderRepository.findWithSelectFolders(driveFolderId);

        List<MyDriveViewDTO> myDriveViewDTOList = MyDriveFolders.stream()
                .map(folder -> modelMapper.map(folder, MyDriveViewDTO.class))
                .collect(Collectors.toList());


        return myDriveViewDTOList;
    }
}
