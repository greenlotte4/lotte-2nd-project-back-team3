
package BackAnt.controller.board;

import BackAnt.dto.board.BoardFileDTO;
import BackAnt.dto.board.BoardResponseViewDTO;
import BackAnt.entity.board.BoardFile;
import BackAnt.service.board.BoardFileService;
import BackAnt.service.board.BoardFileValidatorService;
import BackAnt.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
    날 짜 : 2024/12/10(화)
    담당자 : 김민희
    내 용 : Board File 를 위한 Controller 생성

    수정 내역:

*/

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board")
public class BoardFileController {

    private final BoardService boardService;
    private final BoardFileService boardFileService;
    private final BoardFileValidatorService boardFileValidatorService;


    // 글쓰기 (파일 업로드)
//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadFile(BoardFileDTO.UploadRequest uploadRequest) {
//
//        log.info("여기는 컨트롤러(BoardFile write) ---------------------------------");
//        //log.info("게시글 정보: {}", request);
//
//        try {
//            BoardFileDTO.UploadResponse response = boardFileService.uploadFile(uploadRequest);
//            return ResponseEntity.ok(response);
//
//        } catch (IllegalArgumentException e) {
//            log.warn("파일 유효성 검사 실패: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            log.error("파일 업로드 중 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }

    // 글쓰기 (파일 업로드)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestPart("uploadRequest") BoardFileDTO.UploadRequest uploadRequest,
            @RequestPart("file") MultipartFile file) {

        log.info("여기는 컨트롤러(BoardFile write) ---------------------------------");
        log.info("업로드 요청 데이터: {}", uploadRequest);
        log.info("업로드 파일: {}", file.getOriginalFilename());

        try {
            // 파일을 DTO에 직접 설정
            uploadRequest.setBoardFile(file);

            // 서비스 호출
            BoardFileDTO.UploadResponse response = boardFileService.uploadFile(uploadRequest);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("파일 유효성 검사 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("파일 업로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // 글 상세 조회 - (파일 다운로드 기능 화면 조회)
    @GetMapping("/view/{id}/files")
    public List<BoardFile> boardFileDownload(@PathVariable int id) {
        log.info("파일 다운로드 기능 컨트롤러 getBoardFilesById!!!" + id);
        //return boardFileService.boardFileDownload(id);
        List<BoardFile> listBoardFile = boardFileService.getBoardFiles(id);

        log.info("파일 목록: "+listBoardFile);

        return listBoardFile;
    }


    // 파일 다운로드 처리
    @GetMapping("/files/download/{boardFileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable int boardFileId) {
        log.info("파일 다운로드 요청 boardFileId: {}", boardFileId);
        return boardFileService.boardFileDownload(boardFileId);
    }
}
