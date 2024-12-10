package BackAnt.service;

import BackAnt.dto.RequestDTO.AttendanceStatusRequestDTO;
import BackAnt.dto.ResponseDTO.UserAttendanceDTO;
import BackAnt.entity.Attendance;
import BackAnt.entity.User;
import BackAnt.entity.enums.AttendanceStatus;
import BackAnt.repository.AttendanceRepository;
import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    // 출근 처리
    public Attendance checkIn(Long userId) {
        // 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));

        // 중복 출근 검증
        validateNoExistingCheckIn(user);

        // 출근 기록 생성
        Attendance attendance = Attendance.builder()
                .user(user)
                .checkIn(LocalDateTime.now())
                .status(AttendanceStatus.CHECKED_IN)
                .build();

        return attendanceRepository.save(attendance);
    }

    // 퇴근 처리
    public Attendance checkOut(Long userId) {
        // 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));

        // 진행 중인 출근 기록 찾기
        Attendance attendance = findOngoingAttendance(user);

        // 퇴근 시간 및 상태 업데이트
        attendance.setCheckOut(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.CHECKED_OUT);

        return attendanceRepository.save(attendance);
    }

    // 상태 업데이트
    public Attendance updateStatus(Long userId, String status) {
        User user = findUserById(userId);
        Attendance attendance = findOngoingAttendance(user);

        attendance.setStatus(AttendanceStatus.valueOf(status.toUpperCase()));
        return attendanceRepository.save(attendance);
    }

    // 현재 출근 상태 가져오기
    public AttendanceStatusRequestDTO getAttendanceStatus(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        log.info("시작" + startOfDay);
        log.info("끝" + endOfDay);

        return attendanceRepository.findTodayAttendanceByUserId(userId, startOfDay, endOfDay)
                .map(attendance -> {
                    String status = attendance.getStatus() != null ? attendance.getStatus().toString() : "AVAILABLE";
                    return new AttendanceStatusRequestDTO(
                            status,
                            attendance.getCheckIn(),
                            attendance.getCheckOut()
                    );
                })
                .orElseGet(() -> new AttendanceStatusRequestDTO("AVAILABLE", null, null));
    }


    ///////////////////////////-- 관리자 기능 --///////////////////////////////////

    public Page<UserAttendanceDTO> getAttendanceSummary(
            Long companyId, String period, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        Page<User> userPage = userRepository.findByCompanyId(companyId, pageable);

        List<Attendance> attendanceList = attendanceRepository.findByCompanyIdAndCheckInBetween(
                companyId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        Map<Long, List<Attendance>> attendanceMap = attendanceList.stream()
                .collect(Collectors.groupingBy(a -> a.getUser().getId()));

        return userPage.map(user -> mapToUserAttendanceDTO(user, attendanceMap, period, startDate, endDate));
    }

    private UserAttendanceDTO mapToUserAttendanceDTO(
            User user,
            Map<Long, List<Attendance>> attendanceMap,
            String period,
            LocalDate startDate,
            LocalDate endDate) {

        List<UserAttendanceDTO.WeeklyRecordDTO> weeklyRecords = new ArrayList<>();
        List<UserAttendanceDTO.MonthlyRecordDTO> monthlyRecords = new ArrayList<>();

        if ("weekly".equalsIgnoreCase(period)) {
            weeklyRecords = createWeeklyRecords(user, attendanceMap, startDate, endDate);
        } else if ("monthly".equalsIgnoreCase(period)) {
            monthlyRecords = createMonthlyRecords(user, attendanceMap, startDate, endDate);
        }

        String totalWorkHours = calculateTotalWorkHours(attendanceMap.getOrDefault(user.getId(), Collections.emptyList()));

        return new UserAttendanceDTO(
                user.getId(),
                user.getName(),
                user.getPosition(),
                user.getDepartment().getName(),
                user.getProfileImageUrl(),
                totalWorkHours,
                weeklyRecords,
                monthlyRecords
        );
    }

    private List<UserAttendanceDTO.WeeklyRecordDTO> createWeeklyRecords(
            User user,
            Map<Long, List<Attendance>> attendanceMap,
            LocalDate startDate,
            LocalDate endDate) {

        List<UserAttendanceDTO.WeeklyRecordDTO> weeklyRecords = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            LocalDate tempDate = currentDate; // effectively final 변수로 사용

            List<Attendance> dailyAttendance = attendanceMap
                    .getOrDefault(user.getId(), Collections.emptyList())
                    .stream()
                    .filter(a -> a.getCheckIn() != null &&
                            a.getCheckIn().toLocalDate().equals(tempDate))
                    .collect(Collectors.toList());

            String checkIn = dailyAttendance.isEmpty() ? "-"
                    : dailyAttendance.get(0).getCheckIn().toLocalTime().toString();
            String checkOut = dailyAttendance.isEmpty() || dailyAttendance.get(0).getCheckOut() == null
                    ? "-"
                    : dailyAttendance.get(0).getCheckOut().toLocalTime().toString();
            String workTime = dailyAttendance.isEmpty() || dailyAttendance.get(0).getCheckOut() == null
                    ? "-"
                    : calculateWorkTime(dailyAttendance.get(0).getCheckIn(), dailyAttendance.get(0).getCheckOut());

            weeklyRecords.add(new UserAttendanceDTO.WeeklyRecordDTO(tempDate.toString(), checkIn, checkOut, workTime));

            currentDate = currentDate.plusDays(1);
        }

        return weeklyRecords;
    }

    private List<UserAttendanceDTO.MonthlyRecordDTO> createMonthlyRecords(
            User user,
            Map<Long, List<Attendance>> attendanceMap,
            LocalDate startDate,
            LocalDate endDate) {

        List<UserAttendanceDTO.MonthlyRecordDTO> monthlyRecords = new ArrayList<>();
        LocalDate tempWeekStart = startDate;
        int weekNumber = 1;

        while (!tempWeekStart.isAfter(endDate)) {
            LocalDate currentWeekStart = tempWeekStart;
            LocalDate currentWeekEnd = currentWeekStart.plusDays(6).isAfter(endDate)
                    ? endDate
                    : currentWeekStart.plusDays(6);

            List<Attendance> weeklyAttendance = attendanceMap
                    .getOrDefault(user.getId(), Collections.emptyList())
                    .stream()
                    .filter(a -> a.getCheckIn() != null &&
                            !a.getCheckIn().toLocalDate().isBefore(currentWeekStart) &&
                            !a.getCheckIn().toLocalDate().isAfter(currentWeekEnd))
                    .collect(Collectors.toList());

            String total = calculateWeeklyTotalTime(weeklyAttendance);

            monthlyRecords.add(new UserAttendanceDTO.MonthlyRecordDTO(
                    weekNumber + "주", total, total, "0h 0m 0s", "0h 0m 0s"
            ));

            tempWeekStart = tempWeekStart.plusDays(7);
            weekNumber++;
        }

        return monthlyRecords;
    }
    private String calculateWeeklyTotalTime(List<Attendance> weeklyAttendance) {
        if (weeklyAttendance == null || weeklyAttendance.isEmpty()) {
            return "0h 0m"; // 데이터가 없을 경우 기본값 반환
        }

        long totalMinutes = weeklyAttendance.stream()
                .filter(a -> a.getCheckIn() != null && a.getCheckOut() != null) // 유효한 데이터만 계산
                .mapToLong(a -> Duration.between(a.getCheckIn(), a.getCheckOut()).toMinutes()) // 분 단위로 시간 계산
                .sum();

        long hours = totalMinutes / 60; // 시간 계산
        long minutes = totalMinutes % 60; // 분 계산
        return hours + "h " + minutes + "m"; // 문자열로 반환
    }

    private String calculateWorkTime(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkIn == null || checkOut == null) {
            return "0h 0m";
        }
        Duration duration = Duration.between(checkIn, checkOut);
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        return hours + "h " + minutes + "m";
    }

    private String calculateTotalWorkHours(List<Attendance> attendances) {
        long totalMinutes = attendances.stream()
                .filter(a -> a.getCheckOut() != null && a.getCheckIn() != null)
                .mapToLong(a -> Duration.between(a.getCheckIn(), a.getCheckOut()).toMinutes())
                .sum();

        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return hours + "h " + minutes + "m";
    }

    ///////////////////////////-- Helper Methods --///////////////////////////////////

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
    }

    // 진행 중인 출근 기록 찾기
    private Attendance findOngoingAttendance(User user) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        return attendanceRepository.findTodayAttendanceByUserId(user.getId(), startOfDay, endOfDay)
                .orElseThrow(() -> new IllegalStateException("진행 중인 출근 기록이 없습니다."));
    }

    // 중복 출근 검증
    private void validateNoExistingCheckIn(User user) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        log.info("여기?");

        if (attendanceRepository.findTodayAttendanceByUserId(user.getId(), startOfDay, endOfDay).isPresent()) {
            log.info("여기2?");
            throw new IllegalStateException("오늘의 출근 기록이 이미 존재합니다.");
        }
    }
}
