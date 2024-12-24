package BackAnt.dto.board;
import lombok.*;

/*
    날 짜 : 2024/12/08(일)
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
public class BoardUpdateRequestDTO {

    private String title;
    private String content;



}
