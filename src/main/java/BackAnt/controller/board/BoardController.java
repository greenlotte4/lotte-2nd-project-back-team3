package BackAnt.controller.board;

import BackAnt.dto.board.BoardDTO;
import BackAnt.dto.board.BoardPageDTO;
import BackAnt.dto.board.BoardResponseViewDTO;
import BackAnt.dto.board.BoardSearchDTO;
import BackAnt.dto.common.ResponseDTO;
import BackAnt.entity.board.Board;
import BackAnt.service.board.BoardService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.security.web.savedrequest.FastHttpDateFormat.formatDate;

/*
    날 짜 : 2024/12/02(월)
    담당자 : 김민희
    내 용 : Board 를 위한 Controller 생성

    수정 내역:
    2024/12/09(월) - 김민희 : 글 수정 시 글 작성자만 수정권한 가지도록 구현
    2024/12/10(화) - 김민희 : 글 목록 전체 조회 시 -> 커스텀 매핑 추가
*/

@CrossOrigin(origins = "*")  // 모든 출처 허용 (개발단계)
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;
    private final ModelMapper modelMapper;


    // 글 목록 전체 조회
    @GetMapping("/list")
    public ResponseEntity<Page<BoardDTO>> getFindAllBoards(
            @PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("(컨트롤러) 게시글 목록 조회 요청 -------------------------");
        Page<BoardDTO> boards = boardService.getFindAllBoards(pageable);
        log.info("(컨트롤러) 게시글 목록 boardDTO :  "+boards);
        log.info("(컨트롤러) 총 페이지 수: " + boards.getTotalPages());
        log.info("(컨트롤러) 전체 게시글 수: " + boards.getTotalElements());
        log.info("(컨트롤러) 현재 페이지 번호: " + boards.getNumber());
        return ResponseEntity.ok(boards);
    }

    // 댓글 카운트
    @GetMapping("/{boardId}/comments/count")
    public int getCommentCount(@PathVariable Long boardId) {
        return boardService.getCommentCount(boardId);
    }


    // 글 검색
    @GetMapping("/list/search")
    public ResponseEntity<Page<BoardSearchDTO>> searchBoards(
            @RequestParam ("type") String type,
            @RequestParam("keyword") String keyword,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10, sort = "regDate",
            direction = Sort.Direction.DESC) Pageable pageable) {

        // 요청 파라미터 확인
        log.info("검색 요청 - 타입: " + type);
        log.info("검색 요청 - 키워드: " + keyword);
        log.info("페이지 번호: " + pageable.getPageNumber());
        log.info("페이지 크기: " + pageable.getPageSize());

        // 검색 서비스 호출
        Page<BoardSearchDTO> searchResults = boardService.searchBoards(type, keyword, pageable);
        log.info("검색 결과 수: " + searchResults.getTotalElements());

        log.info("(컨트롤러) 검색 결과 수: " + searchResults.getTotalElements());
        return ResponseEntity.ok(searchResults);
    }




    // 글 상세 조회
    @GetMapping("/view/{id}")
    public ResponseEntity<BoardResponseViewDTO> getBoardsById(
                                                @PathVariable Long id) {
        // 주어진 ID로 게시글을 조회 시도
        log.info("게시글 ID로 검색 시작 (글 상세 컨트롤러) : " + id);
        BoardResponseViewDTO viewDTO = boardService.getBoardsById(id);

        log.info("BoardDTO 데이터 (글 상세 컨트롤러) : " + viewDTO);
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


    // 글 수정
    @PutMapping("/update/{uid}")
    public ResponseDTO<BoardDTO> updateBoard(
            @PathVariable Long uid,
            @RequestBody BoardDTO boardDTO) {

        log.info("글 수정 컨트롤러 시작: id={}", uid);
        try {
            log.info("글 수정 완료: id={}, title={}", uid, boardDTO.getTitle());
            // 글 수정 권한 확인 및 업데이트 수행
            BoardDTO updatedBoard = boardService.updateBoard(uid, boardDTO);
            log.info("글 수정 완료: id={}, title={}", uid, boardDTO.getTitle());

            return ResponseDTO.success(updatedBoard);

        } catch (EntityNotFoundException e) {
            log.warn("게시글 수정 실패 - 게시글 없음: id={}", uid);
            return ResponseDTO.failure("게시글을 찾을 수 없습니다.");

        } catch (AccessDeniedException e) {
            log.warn("게시글 수정 실패 - 권한 없음: id={}", uid);
            return ResponseDTO.failure("게시글 수정 권한이 없습니다.");

        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생: id={}", uid, e);
            return ResponseDTO.failure("게시글 수정 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }


    // 글 삭제
    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long uid) {
        log.info("🗑️ 게시글 삭제 요청 - 게시글 ID: {}", uid);
        boardService.deleteBoard(uid);
        return ResponseEntity.noContent().build();
    }

    // 카테고리별 게시글 조회 - 2024/12/23 강은경
    @GetMapping("/select/{categoryId}")
    public ResponseEntity<List<BoardDTO>> getBoardsByCategory(@PathVariable Long categoryId) {
        List<BoardDTO> boards = boardService.getBoardsByCategory(categoryId);
        return ResponseEntity.ok(boards);
    }

}
