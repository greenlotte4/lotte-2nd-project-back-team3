package BackAnt.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private int priority; // 0: 낮음, 1: 보통, 2: 높음
    private int status; // 0: 미완료, 1: 완료
    private String size; // 작업 크기
    private LocalDate dueDate; // 작업 마감일
    private int position; // 작업 순서
    private Long stateId; // 상태 ID
    private Long assignedUserId; // 작업 담당자 ID
    private String assignedUserName; // 작업 담당자 이름 (추가)
    private LocalDateTime createdAt; // 생성 날짜
    private LocalDateTime updatedAt; // 수정 날짜
}
