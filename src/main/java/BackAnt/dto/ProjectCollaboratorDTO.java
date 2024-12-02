package BackAnt.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProjectCollaboratorDTO {
    private Long id;
    private Long projectId; // 프로젝트 ID
    private Long userId; // 사용자 ID
    private String username; // 사용자 이름
    private int type; // 프로젝트 권한 (0:ADMIN, 1:WRITE, 2:READ)
    private LocalDateTime invitedAt; // 초대 날짜
}
