package BackAnt.dto.board;

import lombok.*;

import java.time.LocalDateTime;

/*
    날 짜 : 2024/12/02(월)
    담당자 : 김민희
    내 용 : Board 를 위한 BoardDTO 생성
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {

    private Long id; // 게시글 번호

    private String cate1; // 카테고리1
    private String cate2; // 카테고리2

    private String title;    // 게시글 제목

    private int comment = 0; // 게시글 댓글 0

    private String content;  // 게시글 내용

    private String writer;   // 작성자

    private int file = 0; // 파일 0

    private int hit = 0; // 조회수 처음에 0

    private int likes = 0; // 좋아요 처음에 0

    private String regIp; // 작성일시

    private LocalDateTime regDate; // 작성일

}
