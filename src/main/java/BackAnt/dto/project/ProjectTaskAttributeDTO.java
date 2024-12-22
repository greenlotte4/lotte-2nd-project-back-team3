package BackAnt.dto.project;

import BackAnt.entity.enums.AttributeType;
import lombok.*;

/*
    날짜 : 2024/12/22
    이름 : 강은경
    내용 : ProjectTaskAttributeDTO 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProjectTaskAttributeDTO {
    private Long id;           // 속성 ID (등록 요청 시 null 가능)
    private String name;       // 속성 이름 (예: P0 - 긴급, S - 소형 등)
    private AttributeType type; // 속성 유형 (PRIORITY 또는 SIZE)
}
