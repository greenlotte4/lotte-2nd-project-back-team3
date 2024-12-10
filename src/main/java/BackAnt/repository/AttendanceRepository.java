package BackAnt.repository;

import BackAnt.entity.Attendance;
import BackAnt.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // 특정 사용자의 진행 중인 출근 기록 찾기
    Optional<Attendance> findByUserAndCheckOutIsNull(User user);

    // 오늘 날짜 특정 사용자 출퇴근 기록 조회
    @Query("SELECT a FROM Attendance a " +
            "WHERE a.user.id = :userId AND a.createdAt >= :startOfDay AND a.createdAt < :endOfDay " +
            "ORDER BY a.createdAt DESC")
    Optional<Attendance> findTodayAttendanceByUserId(@Param("userId") Long userId,
                                                     @Param("startOfDay") LocalDateTime startOfDay,
                                                     @Param("endOfDay") LocalDateTime endOfDay);


    // 회사 필터 출퇴근 전체 기록 조회 + (페이징)
    @Query("SELECT a FROM Attendance a WHERE a.user.company.id = :companyId AND a.checkIn BETWEEN :startDate AND :endDate")
    List<Attendance> findByCompanyIdAndCheckInBetween(@Param("companyId") Long companyId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);
}
