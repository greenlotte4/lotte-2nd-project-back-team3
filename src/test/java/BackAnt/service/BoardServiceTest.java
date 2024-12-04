package BackAnt.service;

import BackAnt.dto.BoardDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Test
    void saveTest() {
        BoardDTO boardDTO = BoardDTO.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .cate1("공지")
                .cate2("일반")
                .build();

        Long savedNo = boardService.save(boardDTO);
        assertNotNull(savedNo);
    }
}