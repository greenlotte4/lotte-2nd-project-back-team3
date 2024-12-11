package BackAnt.service.board;
import java.util.Arrays;
import java.util.List;

import BackAnt.JWT.JwtProvider;
import BackAnt.repository.board.BoardFileRepository;
import BackAnt.repository.board.BoardRepository;
import BackAnt.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
/*
    날 짜 : 2024/12/10(화)
    담당자 : 김민희
    내 용 : Board 를 위한 File 유효성 검사 Service 생성
*/

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class BoardFileValidatorService {

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;

    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            // 문서 파일
            "txt", "pdf", "doc", "docx", "rtf", "xls", "xlsx", "csv", "ppt", "pptx",
            // 이미지 파일
            "jpg", "jpeg", "png", "gif", "bmp", "tiff", "svg", "eps",
            // 동영상 파일
            "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm",
            // 오디오 파일 (선택적)
            "mp3", "wav", "ogg", "flac", "aac",
            // 압축 파일
            "zip", "rar", "7z", "tar", "gz"
    );

    public static void validate(MultipartFile file) {
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("파일 크기가 10MB를 초과할 수 없습니다.");
        }
        String extension = getFileExtension(file);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다: " + extension);
        }
    }

    private static String getFileExtension(MultipartFile file) {
        return file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                .toLowerCase();
    }


}
