package BackAnt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    private final String USER_DIR = System.getProperty("user.dir"); // 현재 위치에서 /uploads를 붙혀주기때문에 배포 시 문제 없음

    public String uploadImage(MultipartFile file) throws IOException {
        return uploadImage(file, "profile"); // 기본 폴더를 "profile"로 설정
    }

    public String uploadImage(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {   // 파일 유효성 검사
            throw new IllegalArgumentException("No file uploaded.");
        }

        String basePath = "/uploads/" + folder + "/"; // 동적으로 폴더 경로 설정
        String oName = file.getOriginalFilename();
        String sName = System.currentTimeMillis() + "_" + oName; // 타임스탬프 추가

        // 파일 저장 경로 설정
        Path path = Paths.get(USER_DIR + basePath + sName);
        Files.createDirectories(path.getParent()); // 디렉토리 생성
        Files.write(path, file.getBytes()); // 파일 저장

        // 파일 URL 반환
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(basePath)
                .path(sName)
                .toUriString();
    }
}
