package BackAnt.dto.project;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProjectTaskAssignmentDTO {
    private Long id;       // 할당 ID
    private Long taskId;   // 할당된 작업 ID
    private Long userId;   // 할당된 사용자 ID
}
