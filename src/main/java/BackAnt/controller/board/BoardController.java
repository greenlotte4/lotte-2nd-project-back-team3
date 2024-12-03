package BackAnt.controller.board;

import BackAnt.dto.BoardDTO;
import BackAnt.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
    날 짜 : 2024/12/02(월)
    담당자 : 김민희
    내 용 : Board 를 위한 Controller 생성
*/

@Log4j2
@RequiredArgsConstructor
@RestController
public class BoardController {

//    private final BoardService boardService;
//
//    // 글쓰기
//    @PostMapping("/antwork/board/write")
//    public ResponseEntity<BoardDTO> write(@RequestBody BoardDTO boardDTO, HttpServletRequest req) {
//
//        log.info(" 여기는 컨트롤러 (boardDTO) 1 : "+boardDTO);
//
//        boardDTO.setRegIp(req.getRemoteAddr()); // req.getRemoteAddr()로 클라이언트의 IP 주소 가져옴
//        int no = boardService.save(boardDTO);
//
//        req.setAttribute("no", no);
//        return ResponseEntity.ok().body(boardDTO);
//    }
}
