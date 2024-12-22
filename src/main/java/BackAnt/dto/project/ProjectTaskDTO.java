package BackAnt.dto.project;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
/*
    날 짜 : 2024/12/10(화)
    담당자 : 강은경
    내 용 : ProjectTaskDTO 를 위한 DTO 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProjectTaskDTO {
    private Long id;
    private String title; // 작업 제목
    private String content; // 작업 설명
//    private int priority; // 0: 낮음, 1: 보통, 2: 높음
    private int status; // 0: 미완료, 1: 완료
//    private String size; // 작업 크기
    private LocalDate dueDate; // 작업 마감일
    private int position; // 작업 순서
    private Long stateId; // 상태 ID
    private LocalDateTime createdAt; // 생성 날짜
    private LocalDateTime updatedAt; // 수정 날짜

    private Long priorityId;                 // 우선순위 ID
    private String priorityName;             // 우선순위 이름
    private Long sizeId;                     // 크기 ID
    private String sizeName;                 // 크기 이름

    private List<Long> assignedUser; // 작업 담당자 데이터(프론트에서 받아온 작업담당자 insert 하기 위함)
    private List<ProjectAssignedUserDTO> assignedUserDetails; // 해당 작업담당자에 해당하는 user 정보 넘기기 위함
    private String action; // 웹소켓을 전송하기 위한 액션 추가
    private Long projectId;     // 프로젝트 id
}
