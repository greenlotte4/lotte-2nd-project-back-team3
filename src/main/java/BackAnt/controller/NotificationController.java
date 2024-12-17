package BackAnt.controller;

import BackAnt.dto.NotificationDTO;
import BackAnt.entity.Notification;
import BackAnt.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    // 알림 생성 및 WebSocket 전송
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationDTO notificationDTO) {
        notificationService.createAndSendNotification(notificationDTO); // 서비스 호출
        return ResponseEntity.ok("Notification sent successfully");
    }

    // 대상별 알림 조회
    @GetMapping("/history/{targetType}/{targetId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByTarget(targetType, targetId);
        return ResponseEntity.ok(notifications);
    }

    // 알림 읽음 처리
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        log.info("알림읽음 " + id);
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    // 알림 조회 API
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @RequestParam("targetId") Long targetId) {
        if (targetId == null) {
            return ResponseEntity.badRequest().build(); // targetId가 없으면 400 Bad Request 반환
        }

        List<NotificationDTO> notifications = notificationService.getNotificationsByTargetId(targetId);
        return ResponseEntity.ok(notifications); // 조회된 알림 반환
    }


    // 특정 senderId로 보낸 알림 목록 조회
    @GetMapping("/sent/{senderId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsBySenderId(@PathVariable Long senderId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsBySenderId(senderId);
        return ResponseEntity.ok(notifications);
    }
}
