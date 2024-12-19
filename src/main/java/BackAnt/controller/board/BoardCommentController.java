package BackAnt.controller.board;

import BackAnt.dto.ResponseDTO.ApiResponseDTO;
import BackAnt.dto.board.comment.BoardCommentDTO;
import BackAnt.dto.board.comment.BoardCommentRequestDTO;
import BackAnt.dto.board.comment.BoardCommentUpdateDTO;
import BackAnt.service.board.BoardCommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/board/comment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    // 게시판 댓글 작성
    @PostMapping("/{boardId}")
    public ResponseEntity<ApiResponseDTO<BoardCommentDTO>> createBoardComment(
            @PathVariable Long boardId, // URL에서 boardId 받음
            @RequestBody BoardCommentRequestDTO requestDTO, // 요청 바디에서 DTO 받음
            HttpServletRequest request) {

        log.info("댓글 작성 시작 - 게시글 번호: {}, 요청자: {}", boardId, requestDTO.getUserId());
        log.info("댓글 디티오 확인 "+ requestDTO.toString());

        try {
            BoardCommentDTO savedComment = boardCommentService.createBoardComment(
                    boardId, requestDTO.getUserId(), requestDTO, request);
                            // userId를 DTO에서 직접 가져옴
            log.info("댓글 작성 완료 - 댓글 번호: {}", savedComment.getId());
            return ResponseEntity.ok(new ApiResponseDTO<>
                    (true, "댓글이 작성되었습니다 🧤", savedComment));

        } catch (Exception e) {
            log.error("댓글 작성 실패 - 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.fail(e.getMessage()));
        }
    }


    // 게시판 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponseDTO<BoardCommentDTO>> updateBoardComment(
            @PathVariable Long commentId,
            @RequestBody BoardCommentUpdateDTO updateDTO) {
        log.info("댓글 수정 시작 - 댓글 번호: {}, 수정자: {}", commentId, updateDTO.getUserId());

        try {
            BoardCommentDTO updatedComment = boardCommentService.updateBoardComment(
                    commentId, updateDTO.getUserId(), updateDTO);
            log.info("댓글 수정 완료 - 댓글 번호: {}", updatedComment.getId());
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "댓글이 수정되었습니다.", updatedComment));
        } catch (Exception e) {
            log.error("댓글 수정 실패 - 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.fail(e.getMessage()));
        }
    }

    // 게시판 댓글 삭제
    @DeleteMapping("/{commentId}/{userId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteBoardComment(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        log.info("(컨트롤러) 댓글 삭제 시작 - 댓글 번호: {}, 삭제자: {}", commentId, userId);

        try {
            boardCommentService.deleteBoardComment(commentId, userId);
            log.info("(컨트롤러) 댓글 삭제 완료 - 댓글 번호: {}", commentId);
            log.info("(컨트롤러) 응답 데이터: {}", new ApiResponseDTO<>(true, "댓글 삭제 성공", null));

            return ResponseEntity.ok(new ApiResponseDTO<>(true, "댓글이 삭제되었습니다.", null));
        } catch (Exception e) {
            log.error("(컨트롤러) 댓글 삭제 실패 - 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.fail(e.getMessage()));
        }
    }

    // 게시판 댓글 목록 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponseDTO<List<BoardCommentDTO>>> getBoardCommentList(
            @PathVariable Long boardId,
            @RequestParam Long userId) {
        log.info("(컨트롤러) 댓글 목록 조회 시작 - 게시글 번호: {}, 요청자: {}, 관리자여부: {}",
                boardId, userId);

        try {
            List<BoardCommentDTO> comments = boardCommentService.getBoardCommentList(boardId, userId);
            log.info("(컨트롤러) 댓글 목록 조회 완료 - 게시글 번호: {}, 조회된 댓글 수: {}",
                    boardId, comments.size());
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "댓글 목록 조회 완료", comments));
        } catch (Exception e) {
            log.error("(컨트롤러) 댓글 목록 조회 실패 - 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.fail(e.getMessage()));
        }

    }


    }