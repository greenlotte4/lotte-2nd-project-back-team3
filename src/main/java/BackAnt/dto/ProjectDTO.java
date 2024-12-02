package BackAnt.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProjectDTO {
    private Long id;
    private String projectName; // 프로젝트 이름
    private int status; // 0:진행중, 1:완료
    private LocalDateTime createdAt; // 프로젝트 생성 날짜
    private List<ProjectStateDTO> states; // 프로젝트 상태 목록
}
