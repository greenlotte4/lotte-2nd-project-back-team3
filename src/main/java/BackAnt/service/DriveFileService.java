package BackAnt.service;

import BackAnt.dto.drive.DriveNewFileInsertDTO;
import BackAnt.dto.drive.DriveNewFolderInsertDTO;
import BackAnt.entity.DriveFileEntity;
import BackAnt.repository.DriveFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Log4j2
@Service
public class DriveFileService {

    private final DriveFileRepository driveFileRepository;
    private final ModelMapper modelMapper;
    private final String USER_DIR = System.getProperty("user.dir"); // 현재 위치에서 /uploads를 붙혀주기때문에 배포 시 문제 없음



    public List<DriveNewFileInsertDTO> fileInsert(DriveNewFileInsertDTO driveNewFileInsertDTO) {
        List<MultipartFile> driveFiles = driveNewFileInsertDTO.getDriveFiles();
        String parentFolderPath = "/uploads/drive";

        if (driveFiles == null || driveFiles.isEmpty()) {
            return Collections.emptyList();  // 파일이 없으면 빈 리스트 반환
        }

        List<DriveNewFileInsertDTO> fileDTOs = new ArrayList<>();  // 파일 정보를 저장할 리스트



        if(driveFiles == null || driveFiles.size() == 0) {
            return null;
        }
        for(MultipartFile driveFile : driveFiles) {
            String driveFileName = driveFile.getOriginalFilename();
            String driveFileSName = System.currentTimeMillis() + "_" + driveFileName;
            String driveFileMaker = driveNewFileInsertDTO.getDriveFileMaker();
            String ext = driveFileName.substring(driveFileName.lastIndexOf("."));
            String driveFileOName = UUID.randomUUID().toString() + ext;

            String newFolderPath = parentFolderPath + "/" + driveFileSName;
            Path folderPath = Paths.get(USER_DIR + newFolderPath); // 폴더 경로 생성
            Path folderP = Paths.get(USER_DIR + parentFolderPath);
            Path folderPaths = Paths.get(USER_DIR+"/uploads/drive/");

            long fileSize = driveFile.getSize(); // 파일 크기 (바이트 단위)
            double fileSizeInKB = fileSize / 1024.0; // 바이트를 KB로 변환
            double roundedFileSizeInKB = Math.round(fileSizeInKB * 100.0) / 100.0; // 소수점 2자리까지 반올림
            log.info("파일 사이즈: {} KB", roundedFileSizeInKB); // 파일 사이즈 출력



            // 폴더가 없으면 디렉터리 생성
            try {
                Files.createDirectories(folderPaths); // 경로에 디렉터리 생성
            } catch (IOException e) {
                log.error("디렉터리 생성 실패: {}", folderPaths.toString(), e);
                throw new RuntimeException("디렉터리 생성 실패", e);
            }

            // 실제 파일 저장 경로 (폴더 내에 파일 이름으로 저장)
            Path filePath = folderPaths.resolve(driveFileSName); // 최종 저장 경로

            // 파일 저장
            try {
                Files.copy(driveFile.getInputStream(), filePath);  // 파일 복사
            } catch (IOException e) {
                log.error("파일 저장 실패: {}", filePath.toString(), e);
                throw new RuntimeException("파일 저장 실패", e);
            }

            DriveFileEntity drivefile = DriveFileEntity.builder()
                    .driveFileOName(driveFileOName)
                    .driveFileSName(driveFileSName)
                    .driveFileMaker(driveFileMaker)
                    .driveFileCreatedAt(LocalDateTime.now())
                    .driveFilePath(newFolderPath)
                    .driveFileSize(roundedFileSizeInKB)
                    .build();

            log.info("file이 먼데! : " + drivefile);

            fileDTOs.add(modelMapper.map(driveFileRepository.save(drivefile), DriveNewFileInsertDTO.class));
        }

        return fileDTOs;

    }
}
