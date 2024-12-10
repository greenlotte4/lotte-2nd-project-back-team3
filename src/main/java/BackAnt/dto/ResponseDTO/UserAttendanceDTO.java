package BackAnt.dto.ResponseDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAttendanceDTO {
    private Long userId;
    private String name;
    private String position;
    private String departmentName;
    private String profileImage;
    private String totalWorkHours; // 누적 근무시간 추가

    // 주간 데이터
    private List<WeeklyRecordDTO> weeklyRecords;

    // 월간 데이터
    private List<MonthlyRecordDTO> monthlyRecords;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WeeklyRecordDTO {
        private String date; // 날짜
        private String checkIn; // 출근 시간
        private String checkOut; // 퇴근 시간
        private String workTime; // 근무 시간
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyRecordDTO {
        private String week; // 주차 (1주, 2주 등)
        private String total; // 총 근무 시간
        private String basic; // 기본 근무 시간
        private String overtime; // 연장 근무 시간
        private String night; // 야간 근무 시간
    }
}
