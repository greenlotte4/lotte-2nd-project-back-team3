package BackAnt.dto.board;

import BackAnt.entity.User;
import BackAnt.entity.board.Board;
import BackAnt.service.board.BoardCategoryService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/*
    날 짜 : 2024/12/24(화)
    담당자 : 김민희
    내 용 : Board 글 검색를 위한 BoardSearchDTO 생성

    수정 내역 :

*/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardSearchDTO {

    private Long id;               // 게시글 ID
    private String title;          // 게시글 제목
    private String writerName;     // 작성자 이름
    private LocalDateTime regDate; // 작성일

}
