package BackAnt.controller.board;

import BackAnt.dto.BoardDTO;
import BackAnt.entity.Board;
import BackAnt.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    날 짜 : 2024/12/02(월)
    담당자 : 김민희
    내 용 : Board 를 위한 Controller 생성
*/

// @CrossOrigin(origins = "http://localhost:5173") // 프론트 개발 환경
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
    public ResponseEntity<Board> getBoardsById(@PathVariable Long id){
        Board board = boardService.getBoardsById(id);
        return ResponseEntity.status(HttpStatus.OK).body(board);
    }

    // 글쓰기
    @PostMapping("/write")
    public ResponseEntity<Long> insertBoard(@RequestBody BoardDTO boardDTO) {
        log.info("여기는 컨트롤러(insertBoard)");
        log.info("boardDTO " + boardDTO);

        return ResponseEntity.ok(boardService.save(boardDTO));
    }


}
