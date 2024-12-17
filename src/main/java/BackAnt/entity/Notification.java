package BackAnt.entity;

import BackAnt.util.JpaJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/*
    날짜 : 2024/12/9
    이름 : 최준혁
    내용 : 알림 관리를 위한 Notification 엔티티 생성
*/
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String targetType; // USER, DEPARTMENT, COMPANY

    @Column(nullable = false)
    private Long targetId; // 대상 ID

    @Column(nullable = false)
    private String message; // 알림 메시지

    @Convert(converter = JpaJsonConverter.class)
    private Map<String, Object> metadata; // 추가 정보

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isRead = false; // 기본값을 false로 설정

    @Column(name = "sender_id", nullable = false)
    private Long senderId; // 보낸 사람의 ID
}
