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
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class DriveFolderService {

    private final DriveFolderRepository driveFolderRepository;
    private final ModelMapper modelMapper;

    //상위추적경로
    public String buildFolderPath(String driveFolderId, String parentFolderPath) {
        if (driveFolderId == null) {
            throw new IllegalArgumentException("driveFolderId는 null일 수 없습니다.");
        }

        // 경로를 누적할 스택
        Deque<String> folderStack = new LinkedList<>();

        // MongoDB에서 최상위 폴더까지 반복적으로 부모 폴더를 조회
        while (driveFolderId != null) {
            log.info("현재 추적 중인 폴더 ID: {}", driveFolderId);

            // 데이터베이스에서 현재 폴더 조회
            Optional<DriveFolderDocument> folderOpt = driveFolderRepository.finddriveFolderNameById(driveFolderId);
            if (folderOpt.isPresent()) {
                DriveFolderDocument folder = folderOpt.get();

                // 폴더 이름을 스택에 추가
                folderStack.push(folder.getDriveFolderName());

                // 부모 폴더 ID를 얻음, 없으면 null
                driveFolderId = folder.getDriveParentFolderId(); // 필드가 없으면 null 반환
                log.info("이 ID는 먼데? : " + driveFolderId);
            } else {
                // 폴더를 찾을 수 없을 경우 예외 발생
                throw new RuntimeException("폴더를 찾을 수 없습니다: " + driveFolderId);
            }
        }

        // 기본 경로에 상위 폴더들을 연결
        StringBuilder fullPath = new StringBuilder(parentFolderPath);
        while (!folderStack.isEmpty()) {
            fullPath.append("/").append(folderStack.pop());
        }

        log.info("최종 생성된 경로: {}", fullPath);
        return fullPath.toString();
    }

    public DriveNewFolderInsertDTO FolderNewInsert(DriveNewFolderInsertDTO driveNewFolderInsertDTO) {
        String driveFolderId = driveNewFolderInsertDTO.getDriveFolderId(); // 상위 폴더 ID
        String parentFolderPath = "/documents"; // 기본 경로 설정

        // 상위 폴더 경로 계산
        if (driveFolderId != null) {
            parentFolderPath = buildFolderPath(driveFolderId, parentFolderPath); // 상위 경로 추적
        }

        // 새로운 폴더 경로 생성
        String newFolderPath = parentFolderPath + "/" + driveNewFolderInsertDTO.getDriveFolderName();

        // 새로운 폴더 객체 생성
        DriveFolderDocument newFolder = DriveFolderDocument.builder()
                .driveFolderName(driveNewFolderInsertDTO.getDriveFolderName())
                .driveFolderPath(newFolderPath) // 경로 설정
                .driveParentFolderId(driveNewFolderInsertDTO.getDriveFolderId()) // 상위 폴더 ID
                .driveFolderSize(driveNewFolderInsertDTO.getDriveFolderSize())
                .driveFolderCreatedAt(LocalDateTime.now())
                .driveFolderUpdatedAt(LocalDateTime.now())
                .driveFolderMaker(driveNewFolderInsertDTO.getDriveFolderMaker())
                .build();

        log.info("새로 생성된 폴더: " + newFolder);

        // 새로운 폴더 저장 후 DTO로 변환
        return modelMapper.map(driveFolderRepository.save(newFolder), DriveNewFolderInsertDTO.class);
    }


    public List<MyDriveViewDTO> MyDriveView() {
        List<DriveFolderDocument> MyDriveFolders = driveFolderRepository.findFirstWithFolders();

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
