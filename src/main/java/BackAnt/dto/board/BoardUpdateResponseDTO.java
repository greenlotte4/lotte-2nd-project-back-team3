package BackAnt.dto.board;

import lombok.*;

import java.time.LocalDateTime;

/*
    날 짜 : 2024/12/09(월)
    담당자 : 김민희
    내 용 : Board 를 위한 BoardUpdateResponseDTO 생성
           - 글 수정


*/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardUpdateResponseDTO {

    private Long id;            // 게시글 번호
    private String title;       // 수정된 제목
    private String content;     // 수정된 내용
    private Long writerId;      // 작성자 ID만 전달
    private LocalDateTime updateDate;  // 수정 날짜
    private boolean success;    // 수정 성공 여부

    // 필요하다면 파일 관련 정보도 추가
    private int fileCount;      // 첨부된 파일 수


}
