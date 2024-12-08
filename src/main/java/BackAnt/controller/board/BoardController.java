package BackAnt.controller.board;

import BackAnt.dto.board.BoardDTO;
import BackAnt.dto.board.BoardLikeRequestDTO;
import BackAnt.dto.board.BoardResponseViewDTO;
import BackAnt.entity.board.Board;
import BackAnt.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    날 짜 : 2024/12/02(월)
    담당자 : 김민희
    내 용 : Board 를 위한 Controller 생성
*/

@CrossOrigin(origins = "*")  // 모든 출처 허용 (개발단계)
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;


    // 글 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<BoardDTO>> getFindAllBoards(){
        List<BoardDTO> boardDTO = boardService.getFindAllBoards();
        log.info("boardDTO ; "+boardDTO);
        return ResponseEntity.ok(boardDTO);
    }

    // 글 상세 조회
    @GetMapping("/view/{id}")
    public ResponseEntity<BoardResponseViewDTO> getBoardsById(
                                                @PathVariable Long id) {
        BoardResponseViewDTO viewDTO = boardService.getBoardsById(id);
        return ResponseEntity.status(HttpStatus.OK).body(viewDTO);
    }

    // 글 상세 조회 (- 좋아요 기능)
    @PostMapping("/view/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id) {
        try {
            log.info("게시글 좋아요 요청 - 게시글 번호: {}", id);

            boolean isLiked = boardService.toggleLike(id);

            String message = isLiked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.";
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("liked", isLiked);
            response.put("message", message);
            response.put("likeCount", boardService.getLikes(id));

            log.info("게시글 좋아요 처리 완료 - 게시글: {}, 결과: {}", id, message);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("게시글 좋아요 처리 실패 (잘못된 요청) - 게시글: {}, 에러: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            log.error("게시글 좋아요 처리 실패 (서버 오류) - 게시글: {}, 에러: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "좋아요 처리 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }


    // 글쓰기
    @PostMapping("/write")
    public ResponseEntity<Long> insertBoard(
            @RequestBody BoardDTO boardDTO, HttpServletRequest req) {
        log.info("여기는 컨트롤러(write) ---------------------------------");
        log.info(" 여기는 컨트롤러(글쓰기) - boardDTO: {}", boardDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(boardService.save(boardDTO, req));
    }

    // 글수정
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateBoard(
//            @PathVariable Long id , @RequestBody BoardDTO boardDTO) {
//
//        boardService.updateBoard(id,boardDTO);
//        return ResponseEntity.ok("글 수정 완료");
//    }



}
