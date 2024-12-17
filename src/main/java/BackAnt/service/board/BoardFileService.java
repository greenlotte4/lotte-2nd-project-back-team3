package BackAnt.service.board;

import BackAnt.JWT.JwtProvider;
import BackAnt.dto.board.BoardFileDTO;
import BackAnt.entity.User;
import BackAnt.entity.board.Board;
import BackAnt.entity.board.BoardFile;
import BackAnt.repository.UserRepository;
import BackAnt.repository.board.BoardFileRepository;
import BackAnt.repository.board.BoardRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

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

    // 파일 업로드 기본 경로 설정
    private final Path UPLOAD_BASE_PATH = Paths.get(System.getProperty("user.dir"), "uploads", "boardFiles");


    // ModelMapper 커스텀 설정
    @PostConstruct
    private void configureModelMapper() {
        // 업로드 요청 매핑
        modelMapper.createTypeMap(BoardFile.class, BoardFileDTO.UploadRequest.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getBoardFileMaker().getId(), BoardFileDTO.UploadRequest::setWriterId);
                    mapper.map(src -> src.getBoard().getId(), BoardFileDTO.UploadRequest::setBoardId);
                });

        // 업로드 응답 매핑
        modelMapper.createTypeMap(BoardFile.class, BoardFileDTO.UploadResponse.class)
                .addMappings(mapper -> {
                    mapper.map(BoardFile::getBoardFileId, BoardFileDTO.UploadResponse::setBoardFileId);
                    mapper.map(BoardFile::getBoardFileOName, BoardFileDTO.UploadResponse::setBoardFileOName);
                    mapper.map(BoardFile::getBoardFileSize, BoardFileDTO.UploadResponse::setBoardFileSize);
                    mapper.map(BoardFile::getBoardFileExt, BoardFileDTO.UploadResponse::setBoardFileExt);
                    mapper.map(BoardFile::getBoardFileUploadedAt, BoardFileDTO.UploadResponse::setBoardFileUploadedAt);
                });

        // 다운로드 정보 매핑
        modelMapper.createTypeMap(BoardFile.class, BoardFileDTO.DownloadInfoDTO.class)
                .addMappings(mapper -> {
                    mapper.map(BoardFile::getBoardFileSName, BoardFileDTO.DownloadInfoDTO::setBoardFileSName);
                    mapper.map(BoardFile::getBoardFileOName, BoardFileDTO.DownloadInfoDTO::setBoardFileOName);
                });
    }

    // 글 상세 조회 (파일 다운로드 화면 조회)
    public List<BoardFile> getBoardFiles(int boardFileId){
        log.info("(서비스) 파일 다운로드 화면 조회 boardFileId: {}", boardFileId);
        List<BoardFile> listBoardFile = boardFileRepository.findByBoardId(boardFileId);
        log.info(listBoardFile);
        return listBoardFile;
    }

    // 파일 유효성 검사 (사이즈, 확장자, 이름)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList(
                    // 문서 파일
                    "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "rtf", "csv", "xml", "html", "htm",
                    "hwp", "hwpx", "cell", "show", // 한글과컴퓨터 관련
                    "odt", "ods", "odp", // OpenDocument 형식

                    // 이미지 파일
                    "jpg", "jpeg", "png", "gif", "bmp", "tif", "tiff", "raw", "webp", "svg", "ico", "heic",

                    // 압축 파일
                    "zip", "rar", "7z", "tar", "gz", "alz",

                    // 오디오 파일
                    "mp3", "wav", "wma", "ogg", "flac", "m4a", "mid", "midi",

                    // 비디오 파일
                    "mp4", "avi", "wmv", "mov", "mkv", "flv", "webm", "m4v", "3gp",

                    // CAD 및 설계 파일
                    "dwg", "dxf", "dwf", "step", "stp",

                    // 프로그래밍 및 개발 관련
                    "java", "class", "jar", "js", "jsx", "ts", "tsx", "json", "sql", "php", "py", "cpp", "c", "h", "cs", "rb",

                    // 폰트 파일
                    "ttf", "otf", "woff", "woff2",

                    // 기타 일반 파일
                    "log", "bak", "tmp", "conf", "ini", "properties",

                    // 이메일 관련
                    "eml", "msg",

                    // 데이터베이스 파일
                    "mdb", "accdb", "db", "sqlite"
            )
    );
    private static final int MAX_FILENAME_LENGTH = 100;

    // 파일 유효성 검사
    private void validateFile(MultipartFile file) {
        // 1. 기본 유효성 검사
        if (file == null || file.isEmpty()) {
            log.error("파일 업로드 실패: 파일이 비어있거나 null입니다.");
            throw new IllegalArgumentException("첨부 파일이 없습니다.");
        }

        // 2. 파일 크기 검사
        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("파일 업로드 실패: 파일 크기 초과 - 크기: {}", file.getSize());
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        // 3. 파일명 길이 검사
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.length() > MAX_FILENAME_LENGTH) {
            log.error("파일 업로드 실패: 파일명 길이 초과 - 파일명: {}", originalFilename);
            throw new IllegalArgumentException("파일명은 100자를 초과할 수 없습니다.");
        }

        // 4. 파일 확장자 검사
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            log.error("파일 업로드 실패: 허용되지 않은 파일 확장자 - 확장자: {}", extension);
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다.");
        }

        // 5. MIME 타입 검사
        String contentType = file.getContentType();
        if (contentType == null || !isSafeMimeType(contentType)) {
            log.error("파일 업로드 실패: 안전하지 않은 MIME 타입 - type: {}", contentType);
            throw new IllegalArgumentException("안전하지 않은 파일 형식입니다.");
        }
    }

    // MIME 타입 안전성 검사
    private boolean isSafeMimeType(String mimeType) {
        return mimeType.startsWith("image/") ||
                mimeType.startsWith("application/pdf") ||
                mimeType.startsWith("application/msword") ||
                mimeType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.") ||
                mimeType.startsWith("application/vnd.ms-excel") ||
                mimeType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.");
    }


    // 파일 업로드 처리 (파일을 서버에 저장하고 DB에 파일 정보를 기록)
    @Transactional
    public BoardFileDTO.UploadResponse uploadFile(BoardFileDTO.UploadRequest uploadRequest) {
        MultipartFile file = uploadRequest.getBoardFile();

        // 파일 유효성 검사 실행
        validateFile(file);

        // 파일 유효성 검사
//        if (file == null || file.isEmpty()) {
//            log.error("파일 업로드 실패: 파일이 비어있거나 null입니다.");
//            throw new IllegalArgumentException("첨부 파일이 없습니다.");
//        }

        String originalFileName = file.getOriginalFilename();
        String savedFileName = generateFileName(originalFileName);
        Path fullPath = UPLOAD_BASE_PATH.resolve(savedFileName);

        log.info("파일 업로드 시작 - 원본파일명: {}, 저장파일명: {}", originalFileName, savedFileName);

        // 파일 저장
        saveFile(file, fullPath);

        // 연관 엔티티 설정
        Board board = Board.builder()
                .id(uploadRequest.getBoardId())
                .build();

        User user = User.builder()
                .id(uploadRequest.getWriterId())
                .name(String.valueOf(uploadRequest.getWriterName()))
                .build();

        log.info("파일 업로드 사용자 정보: {}", user);

        // BoardFile 엔티티 생성 및 저장
        BoardFile boardFile = BoardFile.builder()
                .board(board)
                .boardFileMaker(user)  // boardFileMaker -> user로 변경
                .boardFileOName(originalFileName)
                .boardFileSName(savedFileName)
                .boardFilePath(fullPath.toString())
                .boardFileSize(file.getSize())
                .boardFileExt(getFileExtension(originalFileName))
                .build();

        BoardFile savedBoardFile = boardFileRepository.save(boardFile);
        log.info("파일 정보 DB 저장 완료 - 파일ID: {}", savedBoardFile.getBoardFileId());

        return convertToUploadResponse(savedBoardFile);
    }

    // 파일 다운로드 처리
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> boardFileDownload(int boardFileId) {
        log.info("파일 다운로드 요청 - 파일ID: {}", boardFileId);

        BoardFile boardFile = boardFileRepository.findById(boardFileId)
                .orElseThrow(() -> {
                    log.error("파일 다운로드 실패: 파일 정보 없음 - 파일ID: {}", boardFileId);
                    return new FileNotFoundException("파일을 찾을 수 없습니다: " + boardFileId);
                });

        try {
            Path filePath = Paths.get(boardFile.getBoardFilePath()).normalize();
            if (!Files.exists(filePath)) {
                log.error("파일 다운로드 실패: 실제 파일 없음 - 경로: {}", filePath);
                throw new FileNotFoundException("파일이 존재하지 않습니다: " + filePath);
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                log.error("파일 다운로드 실패: 파일 읽기 불가 - 경로: {}", filePath);
                throw new FileNotFoundException("파일을 읽을 수 없습니다: " + filePath);
            }

            String contentType = determineContentType(filePath, boardFile.getBoardFileOName());
            log.info("파일 다운로드 준비 완료 - 파일명: {}, 타입: {}",
                    boardFile.getBoardFileOName(), contentType);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + boardFile.getBoardFileOName() + "\"")
                    .body(resource);

        } catch (IOException e) {
            log.error("파일 다운로드 처리 중 오류 발생", e);
            throw new RuntimeException("파일 다운로드 처리 중 오류 발생", e);
        }
    }


    // 파일의 MIME 타입 결정
    private String determineContentType(Path filePath, String fileName) {
        try {
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = URLConnection.guessContentTypeFromName(fileName);
            }
            return contentType != null ? contentType : "application/octet-stream";
        } catch (IOException e) {
            log.warn("파일 타입 결정 실패, 기본 타입으로 지정 - 파일명: {}", fileName);
            return "application/octet-stream";
        }
    }

    /**
     * 파일 저장
     */
    private void saveFile(MultipartFile file, Path filePath) {
        try {
            Files.createDirectories(UPLOAD_BASE_PATH);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("파일 저장 완료 - 경로: {}", filePath);
        } catch (IOException e) {
            log.error("파일 저장 실패 - 경로: {}", filePath, e);
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    /**
     * 고유한 파일명 생성
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
     * BoardFile 엔티티를 UploadResponse DTO로 변환
     */
    private BoardFileDTO.UploadResponse convertToUploadResponse(BoardFile boardFile) {
        return BoardFileDTO.UploadResponse.builder()
                .boardFileId(boardFile.getBoardFileId())
                .boardFileOName(boardFile.getBoardFileOName())
                .boardFileSize(boardFile.getBoardFileSize())
                .boardFileExt(boardFile.getBoardFileExt())
                .boardFileUploadedAt(boardFile.getBoardFileUploadedAt())
                .build();
    }

    /**
     * 사용자 정의 파일 예외 클래스
     */
    public static class FileNotFoundException extends RuntimeException {
        public FileNotFoundException(String message) {
            super(message);
        }
    }


}