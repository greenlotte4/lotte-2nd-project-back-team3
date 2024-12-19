package BackAnt.dto.board.comment;

import lombok.*;
import java.time.LocalDateTime;

/*
    날 짜 : 2024/12/18 (수)
    담당자 : 김민희
    내 용 : Board 를 위한 BoardCommentUpdateDTO 생성
           - 댓글 수정 요청 DTO

    수정 내역 :
*/

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BoardCommentUpdateDTO {

    private String content;       // 수정할 댓글 내용
    private Boolean secret;       // 비밀 댓글 여부 수정
    private Long userId;          // 수정자 ID
}
