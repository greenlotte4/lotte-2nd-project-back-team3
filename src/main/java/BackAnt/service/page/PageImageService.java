package BackAnt.service.page;

import BackAnt.document.page.PageImageDocument;
import BackAnt.repository.mongoDB.page.PageImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/*
    날 짜 : 2024/11/29(금)
    담당자 : 황수빈
    내 용 : File Upload 를 위한 공유 Service 생성
*/

@RequiredArgsConstructor
@Service
public class PageImageService {

    private final PageImageRepository pageImageRepository; // Page의 Image에 대한 정보를 담음
    private final String USER_DIR = System.getProperty("user.dir"); // 현재 위치에서 /uploads를 붙혀주기때문에 배포 시 문제 없음

    public String saveImage(MultipartFile file) throws IOException {

        if (file.isEmpty()) {   // 파일 유효성 검사
            throw new IllegalArgumentException("No file uploaded.");
        }

        String FilePath = "/uploads/pageImages/";
        String oName = file.getOriginalFilename();
        String sName = System.currentTimeMillis() + "_" + oName; // 타임스탬프 추가

        // 파일 저장 경로 설정
        Path path = Paths.get(USER_DIR + FilePath + sName);
        Files.createDirectories(path.getParent()); // 디렉토리 생성
        Files.write(path, file.getBytes()); // 파일 저장

        // 파일 메타데이터 저장
        PageImageDocument ImageInfo = PageImageDocument.builder()
                .name(sName)
                .path(path.toString()).build();

        pageImageRepository.save(ImageInfo); // 메타데이터 저장

        // 파일 URL 반환
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(FilePath)
                .path(sName)
                .toUriString();
    }
}
