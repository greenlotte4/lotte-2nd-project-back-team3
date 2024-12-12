package BackAnt.dto.project;

import lombok.*;

import java.time.LocalDateTime;
/*
    날 짜 : 2024/12/10(화)
    담당자 : 강은경
    내 용 : ProjectCollaboratorDTO 를 위한 DTO 생성
*/

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

    private String action; // 웹소켓 액션에 따라 처리하기 위함
}
