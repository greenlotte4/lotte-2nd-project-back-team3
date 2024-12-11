package BackAnt.controller.board;

import BackAnt.dto.board.BoardDTO;
import BackAnt.dto.board.BoardResponseViewDTO;
import BackAnt.dto.common.ResponseDTO;
import BackAnt.entity.board.Board;
import BackAnt.service.board.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    // 글 목록 조회
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

            // 커스텀 매핑 설정
            modelMapper.typeMap(Board.class, BoardDTO.class).addMappings(mapper ->
                    mapper.map(src -> src.getWriter().getId(), BoardDTO::setWriter)
            );

            // Page<Board>를 Page<BoardDTO>로 변환
            Page<BoardDTO> boardDTOs = boards.map(board -> modelMapper.map(board, BoardDTO.class));
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
        BoardResponseViewDTO viewDTO = boardService.getBoardsById(id);
        return ResponseEntity.status(HttpStatus.OK).body(viewDTO);
    }

//    // 글 상세 조회 (- 파일 다운로드 기능)
//    @GetMapping("/view/{id}/files")
//    public ResponseEntity<BoardResponseViewDTO> getBoardFilesById(
//            @PathVariable Long id) {
//
//        log.info("getBoardFilesById!!!");
//
//
//
//        BoardResponseViewDTO viewDTO = boardService.getBoardsById(id);
//
//
//        return ResponseEntity.status(HttpStatus.OK).body(null);
//    }

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
//    public ResponseEntity<ApiResponseDTO<BoardDTO>> updateBoard(
//            @PathVariable Long id,
//            @RequestBody BoardDTO boardDTO) {
//        try {
//            log.info("게시글 수정 요청 - 게시글 번호: {}", id);
//
//            Board updatedBoard = boardService.updateBoard(id, BoardDTO);
//            BoardDTO boardDTO = BoardDTO.from(updatedBoard);
//
//            // 성공 응답
//            return ResponseEntity.ok(
//                    new ApiResponseDTO<>(
//                            true,
//                            "게시글이 성공적으로 수정되었습니다.",
//                            boardDTO
//                    )
//            );
//
//        } catch (IllegalArgumentException e) {
//            log.error("게시글 수정 실패 (잘못된 요청) - 게시글: {}, 에러: {}", id, e.getMessage());
//            return ResponseEntity.badRequest()
//                    .body(ApiResponseDTO.fail(e.getMessage()));
//
//        } catch (Exception e) {
//            log.error("게시글 수정 실패 (서버 오류) - 게시글: {}, 에러: {}", id, e.getMessage());
//            return ResponseEntity.internalServerError()
//                    .body(ApiResponseDTO.fail("게시글 수정 중 오류가 발생했습니다."));
//        }
//    }

    // 글 수정
    @PutMapping("/update/{id}")
    public ResponseDTO<BoardDTO> updateBoard(
            @PathVariable Long id,
            @RequestBody BoardDTO boardDTO) {

        log.info("글 수정 컨트롤러");
        try {
            BoardDTO updatedBoard = boardService.updateBoard(id, boardDTO);
            log.info("글 수정 컨트롤러 id"+id);
            log.info("글 수정 컨트롤러 boardDTO"+boardDTO);

            return ResponseDTO.success(updatedBoard);
        } catch (Exception e) {
            return ResponseDTO.failure(e.getMessage());
        }
    }


    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        log.info("🗑️ 게시글 삭제 id: {}", id);
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
