package BackAnt.service.board;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import BackAnt.JWT.JwtProvider;
import BackAnt.dto.board.BoardDTO;
import BackAnt.dto.board.BoardFileDTO;
import BackAnt.dto.board.BoardResponseViewDTO;
import BackAnt.entity.board.Board;
import BackAnt.entity.User;
import BackAnt.entity.board.BoardFile;
import BackAnt.entity.board.BoardLike;
import BackAnt.repository.board.BoardFileRepository;
import BackAnt.repository.board.BoardLikeRepository;
import BackAnt.repository.board.BoardRepository;
import BackAnt.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/*
    날 짜 : 2024/12/10(화)
    담당자 : 김민희
    내 용 : Board File 를 위한 Service 생성
*/

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class BoardFileService {

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;

    private final String USER_DIR = System.getProperty("user.dir"); // 현재 위치에서 /uploads를 붙혀주기때문에 배포 시 문제 없음
    private final String UPLOAD_DIR = "/uploads/boardFiles/"; // 파일 저장 경로 상수 추가


    // 파일 업로드 처리 메서드
    /**
     * 파일 업로드 처리
     */

    public BoardFileDTO.UploadResponse uploadFile(BoardFileDTO.UploadRequest uploadRequest) {

        MultipartFile file = uploadRequest.getBoardFile();

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("첨부 파일이 없습니다.");
        }

        // 파일 이름 생성 및 저장 경로 설정
        String originalFileName = file.getOriginalFilename();
        String generatedFileName = generateFileName(originalFileName);
        String fullPath = USER_DIR + UPLOAD_DIR + generatedFileName;

        // 파일 저장 로직 실행
        saveFile(file, fullPath);

        Board board = Board.builder()
                .id(uploadRequest.getBoardId())
                .build();

        User user = User.builder()
                .id(uploadRequest.getWriter())
                .build();

        log.info(user);


        // BoardFile 엔티티 저장
        BoardFile boardFile = BoardFile.builder()
                .board(board)
                .boardFileMaker(user)
                .boardFileOName(originalFileName)
                .boardFileSName(generatedFileName)
                .boardFilePath(fullPath)
                .boardFileSize(file.getSize())
                .boardFileExt(getFileExtension(originalFileName))
                .build();

        BoardFile savedBoardFile = boardFileRepository.save(boardFile);

        // 저장된 파일 정보를 DTO로 변환하여 반환
        return modelMapper.map(savedBoardFile, BoardFileDTO.UploadResponse.class);
    }

    /**
     * 파일 이름 생성 (중복 방지)
     */
    private String generateFileName(String originalFileName) {
        String fileExtension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + "." + fileExtension;
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex != -1) ? fileName.substring(dotIndex + 1) : "";
    }

    /**
     * 파일 저장 로직
     */
    private void saveFile(MultipartFile file, String filePath) {
        try {
            // 저장 디렉토리 생성
            Path directory = Paths.get(USER_DIR + UPLOAD_DIR);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // 파일 저장
            file.transferTo(new File(filePath));
            log.info("파일이 성공적으로 저장되었습니다: {}", filePath);
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: {}", filePath, e);
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

//    // 파일 확장자 추출
//    private String getFileExtension(String fileName) {
//        String cleanFileName = Paths.get(fileName).getFileName().toString(); // 경로 제거
//        int dotIndex = cleanFileName.lastIndexOf('.');
//        return (dotIndex != -1) ? cleanFileName.substring(dotIndex + 1) : "";
//    }






    // 글 상세 보기 (파일 다운로드)





}
