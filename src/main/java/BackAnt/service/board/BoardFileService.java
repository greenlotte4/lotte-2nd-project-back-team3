package BackAnt.service.board;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import BackAnt.JWT.JwtProvider;
import BackAnt.dto.board.BoardDTO;
import BackAnt.dto.board.BoardFileDTO;
import BackAnt.dto.board.BoardResponseViewDTO;
import BackAnt.entity.DriveFileEntity;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    수정 내역:
    2024/12/11(수) - 김민희 : 글 상세 조회 파일 다운로드 화면 조회/ 파일 다운로드 기능 구현
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


    // 파일 업로드 처리
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


    // 파일 이름 생성 (중복 방지)
    private String generateFileName(String originalFileName) {
        String fileExtension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + "." + fileExtension;
    }

    // 파일 확장자 추출
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex != -1) ? fileName.substring(dotIndex + 1) : "";
    }

    // 파일 저장 로직
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


    // 글 상세 조회 (파일 다운로드 화면 조회)
    public List<BoardFile> getBoardFiles(int boardFileId){

        log.info("(서비스) 파일 다운로드 화면 조회 boardFileId: {}", boardFileId);
        List<BoardFile> listBoardFile = boardFileRepository.findByBoardId(boardFileId);

        log.info(listBoardFile);
        //BoardFileDTO.UploadResponse boardFileDTO = modelMapper.map(boardFile, BoardFileDTO.UploadResponse.class);
        return listBoardFile;
    }

    // 파일 다운로드 처리
    public ResponseEntity<Resource> boardFileDownload(int boardFileId) {
        try {
            // 데이터베이스에서 파일 정보 조회
            log.info("(Board) 파일 다운로드 요청 boardFileId: {}", boardFileId);
            Optional<BoardFile> boardFile = boardFileRepository.findById(boardFileId);
            if (boardFile.isEmpty()) {
                log.error("파일 정보가 존재하지 않습니다. boardFileId: {}", boardFileId);
                throw new RuntimeException("파일 정보가 존재하지 않습니다: " + boardFileId);
            }

            // 파일 경로 생성 - 이미 전체 경로가 저장되어 있으므로 그대로 사용
            String boardFilePath = boardFile.get().getBoardFilePath();
            String boardFileOName = boardFile.get().getBoardFileOName();
            Path filePath = Paths.get(boardFilePath).normalize();
            log.info("파일 경로 생성 완료: {}", filePath);

            // 파일 존재 여부 확인
            if (!Files.exists(filePath)) {
                log.error("파일이 존재하지 않습니다. 파일 경로: {}", filePath);
                throw new RuntimeException("파일이 존재하지 않습니다: " + filePath);
            }

            // Resource 객체로 파일 로드
            Resource resource = new UrlResource(filePath.toUri());
            log.info("파일 다운로드 요청: {}", resource);
            if (!resource.exists() || !resource.isReadable()) {
                log.error("파일을 읽을 수 없거나 존재하지 않습니다. 파일 경로: {}", filePath);
                throw new RuntimeException("파일을 읽을 수 없거나 존재하지 않습니다: " + filePath);
            }

            // 파일 MIME 타입 확인
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = URLConnection.guessContentTypeFromName(boardFileOName);
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            log.info("파일의 MIME 타입 확인 완료: {}", contentType);


            // HTTP 응답 헤더 설정
            String fileName = filePath.getFileName().toString();
            log.info("파일 다운로드 응답 준비 완료. 파일 이름: {}", fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + boardFileOName + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("파일 다운로드 처리 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 다운로드 처리 중 오류 발생", e);
        }
    }
    public ResponseEntity<Resource> BoardFileDownload(int boardFileId){
        try {
            // 데이터베이스에서 파일 정보 조회
            Optional<BoardFile> boardFile = boardFileRepository.findById(boardFileId);
            if (boardFile.isEmpty()) {
                throw new RuntimeException("파일 정보가 존재하지 않습니다: " + boardFile);
            }

            // 파일 경로 생성
            String oName = boardFile.get().getBoardFileOName();
            Path filePath = Paths.get(USER_DIR + UPLOAD_DIR).normalize();

            // 파일 존재 여부 확인
            if (!Files.exists(filePath)) {
                throw new RuntimeException("파일이 존재하지 않습니다: " + filePath);
            }

            // Resource 객체로 파일 로드
            Resource resource = new UrlResource(filePath.toUri());
            log.info("머라머라: {}", resource);
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("파일을 읽을 수 없거나 존재하지 않습니다: " + filePath);
            }

            String contentType = Files.probeContentType(filePath); // 경로에서 MIME 타입 조회 실패 가능
            if (contentType == null) {
                contentType = URLConnection.guessContentTypeFromName(oName); // 이름에서 추론
            }
            if (contentType == null) {
                contentType = "application/octet-stream"; // 기본 MIME 타입
            }

            // HTTP 응답 헤더 설정
            String fileName = filePath.getFileName().toString();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType)) // MIME 타입 지정
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + oName + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("파일 다운로드 처리 중 오류 발생", e);
        }
    }


}
