package BackAnt.dto.project;

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

    // 추가 필드
    private String uid;
    private String action;  // 웹소켓을 쏴주기 위한 액션
    private Long projectId;

    private Integer companyRate;    // 회사 무료/유료


    // 특정 필드만 받는 생성자
    public ProjectDTO(Long id, String projectName, int status) {
        this.id = id;
        this.projectName = projectName;
        this.status = status;
    }
}
