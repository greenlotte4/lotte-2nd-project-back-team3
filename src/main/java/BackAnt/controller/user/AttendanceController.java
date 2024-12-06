package BackAnt.controller.user;

import BackAnt.entity.Attendance;
import BackAnt.service.AttendanceService;
import BackAnt.util.AttendanceFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;


    // 출근 처리
    @PostMapping("/check-in/{userId}")
    public ResponseEntity<?> checkIn(@PathVariable Long userId) {
        try {
            Attendance attendance = attendanceService.checkIn(userId);
            log.info("출근시간"+ String.valueOf(attendance.getCheckIn()));
            return ResponseEntity.ok( AttendanceFormatter.formatCheckInTime(String.valueOf(attendance.getCheckIn()))); // 출근 기록 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    // 퇴근 처리
    @PostMapping("/check-out/{userId}")
    public ResponseEntity<?> checkOut(@PathVariable Long userId) {
        try {
            Attendance attendance = attendanceService.checkOut(userId);
            log.info("퇴근시간"+ String.valueOf(attendance.getCheckOut()));
            return ResponseEntity.ok( AttendanceFormatter.formatCheckInTime(String.valueOf(attendance.getCheckOut())));// 퇴근 기록 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    // 상태 업데이트
    @PutMapping("/update-status/{userId}")
    public ResponseEntity<?> updateStatus(@PathVariable Long userId, @RequestParam String status) {
        try {
            Attendance attendance = attendanceService.updateStatus(userId, status);
            return ResponseEntity.ok(attendance); // 업데이트된 기록 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

}
