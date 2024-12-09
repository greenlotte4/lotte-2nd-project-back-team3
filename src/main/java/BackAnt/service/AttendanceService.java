package BackAnt.service;

import BackAnt.dto.RequestDTO.AttendanceStatusRequestDTO;
import BackAnt.entity.Attendance;
import BackAnt.entity.User;
import BackAnt.entity.enums.AttendanceStatus;
import BackAnt.repository.AttendanceRepository;
import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    // 출근 처리
    public Attendance checkIn(Long userId) {
        User user = findUserById(userId);
        validateNoExistingCheckIn(user);

        Attendance attendance = Attendance.builder()
                .user(user)
                .checkIn(LocalDateTime.now())
                .status(AttendanceStatus.CHECKED_IN)
                .build();

        return attendanceRepository.save(attendance);
    }

    // 퇴근 처리
    public Attendance checkOut(Long userId) {
        User user = findUserById(userId);
        Attendance attendance = findOngoingAttendance(user);

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
        Optional<Attendance> optionalAttendance = attendanceRepository.findByUserIdAndCheckOutIsNull(userId);

        if (optionalAttendance.isEmpty()) {
            // 기본 상태 반환
            return new AttendanceStatusRequestDTO("AVAILABLE", null, null);
        }

        Attendance attendance = optionalAttendance.get();
        return new AttendanceStatusRequestDTO(
                attendance.getStatus().toString(),
                attendance.getCheckIn(),
                attendance.getCheckOut()
        );
    }



    // Helper: 유저 찾기
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
    }

    // Helper: 기존 출근 기록 확인
    private void validateNoExistingCheckIn(User user) {
        Optional<Attendance> existingRecord = attendanceRepository.findByUserAndCheckOutIsNull(user);
        if (existingRecord.isPresent()) {
            throw new IllegalArgumentException("이미 출근한 상태입니다.");
        }
    }

    // Helper: 진행 중인 출근 기록 찾기
    private Attendance findOngoingAttendance(User user) {
        return attendanceRepository.findByUserAndCheckOutIsNull(user)
                .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다."));
    }
}
