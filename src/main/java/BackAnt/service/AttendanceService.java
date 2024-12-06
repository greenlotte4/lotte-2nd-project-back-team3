package BackAnt.service;

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
        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 이미 출근 기록이 있는지 확인
        Optional<Attendance> existingRecord = attendanceRepository.findByUserAndCheckOutIsNull(user);
        if (existingRecord.isPresent()) {
            throw new IllegalArgumentException("이미 출근한 상태입니다.");
        }

        // 출근 기록 생성
        Attendance attendance = Attendance.builder()
                .user(user)
                .checkIn(LocalDateTime.now())
                .status(AttendanceStatus.CHECKED_IN)
                .build();

        return attendanceRepository.save(attendance); // 출근 기록 반환
    }

    // 퇴근 처리
    public Attendance checkOut(Long userId) {
        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 현재 진행 중인 출근 기록 가져오기
        Attendance attendance = attendanceRepository.findByUserAndCheckOutIsNull(user)
                .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다."));

        // 퇴근 처리
        attendance.setCheckOut(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.CHECKED_OUT);
        return attendanceRepository.save(attendance); // 퇴근 기록 반환
    }

    // 상태 업데이트
    public Attendance updateStatus(Long userId, String status) {
        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 현재 진행 중인 출근 기록 가져오기
        Attendance attendance = attendanceRepository.findByUserAndCheckOutIsNull(user)
                .orElseThrow(() -> new IllegalArgumentException("현재 진행 중인 출근 기록이 없습니다."));

        // 상태 업데이트
        attendance.setStatus(AttendanceStatus.valueOf(status));
        return attendanceRepository.save(attendance); // 업데이트된 출근 기록 반환
    }

}
