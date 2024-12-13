package BackAnt.controller.user;

import BackAnt.dto.RequestDTO.AttendanceStatusRequestDTO;
import BackAnt.dto.ResponseDTO.UserAttendanceDTO;
import BackAnt.entity.Attendance;
import BackAnt.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 현재 출퇴근 상태 확인
    @GetMapping("/status/{userId}")
    public ResponseEntity<?> getAttendanceStatus(@PathVariable Long userId) {
        try {
            log.info("Fetching attendance status for userId: " + userId);
            AttendanceStatusRequestDTO status = attendanceService.getAttendanceStatus(userId);
            log.info("Attendance status retrieved: " + status);
            return ResponseEntity.ok(status);
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error for userId: " + userId, e);
            return ResponseEntity.status(500).body("데이터베이스 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("Unexpected error occurred for userId: " + userId, e);
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    // 출근처리
    @PostMapping("/check-in/{userId}")
    public ResponseEntity<?> checkIn(@PathVariable Long userId) {
        try {
            Attendance attendance = attendanceService.checkIn(userId);
            log.info("출근시간: {}", attendance.getCheckIn());
            // JSON 응답 형식으로 반환
            return ResponseEntity.ok(Map.of(
                    "checkInTime", attendance.getCheckIn(),
                    "status", attendance.getStatus()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    // 퇴근처리
    @PostMapping("/check-out/{userId}")
    public ResponseEntity<?> checkOut(@PathVariable Long userId) {
        try {
            Attendance attendance = attendanceService.checkOut(userId);
            log.info("퇴근시간: {}", attendance.getCheckOut());
            // JSON 응답 형식으로 반환
            return ResponseEntity.ok(Map.of(
                    "checkOutTime", attendance.getCheckOut(),
                    "status", attendance.getStatus()
            ));
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
            return ResponseEntity.ok(attendance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    // 관리자 근태 조회
    @GetMapping("/summary")
    public ResponseEntity<Page<UserAttendanceDTO>> getAttendanceSummary(
            @RequestParam Long companyId,
            @RequestParam String period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("companyId: {}", companyId);
        log.info("period: {}", period);
        log.info("startDate: {}", startDate);
        log.info("endDate: {}", endDate);
        log.info("page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<UserAttendanceDTO> attendanceSummary = attendanceService.getAttendanceSummary(companyId, period, startDate, endDate, pageable);

        return ResponseEntity.ok(attendanceSummary);
    }

}
