package BackAnt.dto.board.comment;

import lombok.*;
import java.time.LocalDateTime;

/*
    날 짜 : 2024/12/18 (수)
    담당자 : 김민희
    내 용 : Board 를 위한 BoardCommentDTO 생성

    수정 내역 :
*/

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BoardCommentDTO {
    private Long id;                    // 댓글 고유 번호
    private String content;             // 댓글 내용
    private Long writerId;
    private String writerName;          // 작성자 이름
    private String writerDepartment;    // 작성자 부서
    private String writerImage;          // 작성자 프로필
    private Boolean secret;             // 비밀 댓글 여부
    private Long parentCommentId;       // 대댓글인 경우 부모 댓글 ID
    private String createdAt;    // 작성일시
    private LocalDateTime updatedAt;    // 수정일시
    private Long boardId; // boardid

}