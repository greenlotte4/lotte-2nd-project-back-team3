package BackAnt.Scheduler;

import BackAnt.entity.User;
import BackAnt.entity.enums.Status;
import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class AnnualLeaveScheduler {

    private final UserRepository userRepository;

    // 매월 1일 자정에 실행
    @Scheduled(cron = "0 0 0 1 * *")
    public void addMonthlyAnnualLeave() {
        log.info("연차 추가 스케줄러 시작");

        // 모든 사용자 중 status가 ACTIVE인 사용자만 조회
        List<User> activeUsers = userRepository.findByStatus(Status.ACTIVE);

        for (User user : activeUsers) {
            // 사용자의 joinDate를 기준으로 한 달 이상 지났는지 확인
            if (user.getJoinDate() != null && user.getJoinDate().isBefore(LocalDate.now().minusMonths(1))) {
                user.setAnnualLeaveTotal(user.getAnnualLeaveTotal() + 1); // 연차 1일 추가
                log.info("연차 추가: 사용자 {}, 총 연차: {}", user.getName(), user.getAnnualLeaveTotal());
            }
        }

        // 업데이트된 사용자 정보 저장
        userRepository.saveAll(activeUsers);

        log.info("연차 추가 스케줄러 완료");
    }
}