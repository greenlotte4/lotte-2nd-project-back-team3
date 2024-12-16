package BackAnt.controller.board;

import BackAnt.dto.board.BoardDTO;
import BackAnt.dto.board.BoardResponseViewDTO;
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
import java.util.HashMap;
import java.util.Map;

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

        log.info("게시글 목록 컨트롤러 시작 -------------");
        log.info("요청받은 페이징 정보: 페이지 번호 = {}, 페이지 크기 = {}, 정렬 = {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        try {
            // 페이징 처리된 Board 엔티티 리스트 가져오기
            Page<Board> boards = boardService.getFindAllBoards(pageable);
            log.info("조회된 Board 데이터 (엔티티): 페이지 번호 = {}, 총 페이지 수 = {}, 총 요소 수 = {}",
                    boards.getNumber(), boards.getTotalPages(), boards.getTotalElements());

            // Page<Board> -> Page<BoardDTO>로 변환
            Page<BoardDTO> boardDTOs = boards.map(board -> {
                BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
                boardDTO.setWriterId(board.getWriter() != null ? board.getWriter().getId() : null);
                boardDTO.setWriterName(board.getWriter() != null ? board.getWriter().getName() : "익명");
                return boardDTO;
            });

            log.info("변환된 BoardDTO 데이터: 페이지 크기 = {}, 변환된 요소 수 = {}",
                    boardDTOs.getSize(), boardDTOs.getContent().size());

            return ResponseEntity.ok(boardDTOs);
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 글 상세 조회
    @GetMapping("/view/{id}")
    public ResponseEntity<BoardResponseViewDTO> getBoardsById(
                                                @PathVariable Long id) {
        // 주어진 ID로 게시글을 조회 시도
        log.info("게시글 ID로 검색 시작 (글 상세 컨트롤러): " + id);
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
//    @PutMapping("/update/{uid}")
//    public ResponseDTO<BoardDTO> updateBoard(
//            @PathVariable Long uid,
//            @RequestBody BoardDTO boardDTO) {
//
//        log.info("글 수정 컨트롤러");
//        try {
//            BoardDTO updatedBoard = boardService.updateBoard(uid, boardDTO);
//            log.info("글 수정 컨트롤러 id"+uid);
//            log.info("글 수정 컨트롤러 boardDTO"+boardDTO);
//
//            return ResponseDTO.success(updatedBoard);
//        } catch (Exception e) {
//            return ResponseDTO.failure(e.getMessage());
//        }
//    }

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
    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long uid) {
        log.info("🗑️ 게시글 삭제 id: {}", uid);
        boardService.deleteBoard(uid);
        return ResponseEntity.noContent().build();
    }
}
