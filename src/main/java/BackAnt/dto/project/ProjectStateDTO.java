package BackAnt.dto.project;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProjectStateDTO {
    private Long id;
    private String title; // 상태 이름
    private String description; // 상태 설명
    private String color; // 상태 색상
    private Long projectId; // 프로젝트 ID
    private List<ProjectTaskDTO> tasks; // 상태 내 작업 목록

    private String action;  // 웹소켓을 쏴주기 위한 액션
}
