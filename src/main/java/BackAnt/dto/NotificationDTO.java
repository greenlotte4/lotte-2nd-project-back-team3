package BackAnt.dto;
/*
    날짜 : 2024/12/9
    이름 : 최준혁
    내용 : NotificationDTO 생성
*/

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private String targetType; // USER, DEPARTMENT, COMPANY
    private Long senderId;
    private Long targetId; // 대상 ID
    private String message; // 알림 메시지
    private Map<String, Object> metadata; // 추가 정보
    private LocalDateTime createdAt; // 생성 시각
    private Boolean isRead; // 읽음 여부
}
