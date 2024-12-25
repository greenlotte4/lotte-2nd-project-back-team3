package BackAnt.service;

import BackAnt.dto.NotificationDTO;
import BackAnt.entity.Notification;
import BackAnt.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/*
    날짜 : 2024/12/9
    이름 : 최준혁
    내용 : 알림 서비스 생성
*/
@Log4j2
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;

    // 알림 생성 및 WebSocket 전송
    public void createAndSendNotification(NotificationDTO dto) {

        // 1. 알림 데이터 저장
        Notification notification = modelMapper.map(dto, Notification.class);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSenderId(dto.getSenderId());
        notificationRepository.save(notification);

        // 2. WebSocket을 통한 실시간 알림 전송
        String destination = "/topic/notifications/" + dto.getTargetId();
        log.info("경로" + destination);
        messagingTemplate.convertAndSend(destination, dto);
    }


    // 대상별 알림 조회
    public List<NotificationDTO> getNotificationsByTarget(String targetType, Long targetId) {
        List<Notification> notifications = notificationRepository.findByTargetTypeAndTargetId(targetType, targetId);
        return notifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // 특정 대상 ID(targetId)에 대한 알림 조회
    public List<NotificationDTO> getNotificationsByTargetId(Long targetId) {
        List<Notification> notifications = notificationRepository.findByTargetId(targetId);

        // Entity -> DTO 변환
        return notifications.stream()
                .map(notification -> NotificationDTO.builder()
                        .id(notification.getId())
                        .message(notification.getMessage())
                        .createdAt(notification.getCreatedAt())
                        .isRead(notification.isRead())
                        .build())
                .collect(Collectors.toList());
    }

    // senderId 기준 알림 조회 및 DTO 변환
    public List<NotificationDTO> getNotificationsBySenderId(Long senderId) {
        List<Notification> notifications = notificationRepository.findBySenderId(senderId);
        return notifications.stream()
                .map(notification -> convertToDTO(notification))
                .collect(Collectors.toList());
    }
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = modelMapper.map(notification, NotificationDTO.class);
        // createdAt을 문자열로 포맷
        dto.setCreatedAt(LocalDateTime.parse(notification.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME)));
        return dto;
    }

}
