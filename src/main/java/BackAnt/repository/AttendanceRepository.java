package BackAnt.repository;

import BackAnt.entity.Attendance;
import BackAnt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // 특정 사용자의 진행 중인 출근 기록 찾기
    Optional<Attendance> findByUserAndCheckOutIsNull(User user);
}
