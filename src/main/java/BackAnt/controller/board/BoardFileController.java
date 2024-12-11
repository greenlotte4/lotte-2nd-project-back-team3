
package BackAnt.controller.board;

import BackAnt.dto.board.BoardFileDTO;
import BackAnt.service.board.BoardFileService;
import BackAnt.service.board.BoardFileValidatorService;
import BackAnt.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(BoardFileDTO.UploadRequest uploadRequest) {

        log.info("여기는 컨트롤러(BoardFile write) ---------------------------------");
        //log.info("게시글 정보: {}", request);

        try {
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


    // 글 보기 (파일 다운로드)

}
