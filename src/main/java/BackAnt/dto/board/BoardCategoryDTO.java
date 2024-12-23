package BackAnt.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
   날 짜 : 2024/12/23(월)
   담당자 : 강은경
   내 용 : BoardCategory 를 위한 DTO 생성


*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCategoryDTO {

    private Long id;   // 카테고리 번호
    private String name; // 카테고리 이름

}
