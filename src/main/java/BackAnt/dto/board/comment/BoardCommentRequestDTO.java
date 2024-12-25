package BackAnt.dto.board.comment;

import lombok.*;
import java.time.LocalDateTime;

/*
    날 짜 : 2024/12/18 (수)
    담당자 : 김민희
    내 용 : Board 를 위한 BoardCommentRequestDTO 생성
          - 댓글 생성 요청 DTO

    수정 내역 :
*/

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BoardCommentRequestDTO {

    private String content;             // 댓글 내용
    private Boolean secret;           // 비밀 댓글 여부
    private Long parentCommentId;       // 대댓글인 경우 부모 댓글 ID
    private Long userId; // 작성자 ID

}
