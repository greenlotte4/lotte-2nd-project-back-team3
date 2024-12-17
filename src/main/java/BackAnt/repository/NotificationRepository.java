package BackAnt.repository;

import BackAnt.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
    날짜 : 2024/12/9
    이름 : 최준혁
    내용 : NotificationRepository 생성
*/
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // targettype과 targetid로 알림조회
    List<Notification> findByTargetTypeAndTargetId(String targetType, Long targetId);

    // targetId로 알림 조회
    List<Notification> findByTargetId(Long targetId);

    // 보낸 사람 기준으로 알림 조회
    List<Notification> findBySenderId(Long senderId);
}
