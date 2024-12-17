package BackAnt.dto.RequestDTO;


import BackAnt.dto.approval.ApproverDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessTripRequestDTO {
    private Long userId; // 사용자 ID
    private String userName; // 사용자 이름
    private String department; // 부서
    private String companyName; // 회사 이름
    private String submissionDate; // 제출 날짜
    private String title; // 출장 제목
    private String organization; // 방문 기관
    private String purpose; // 출장 목적
    private LocalDate startDate; // 시작 날짜
    private LocalDate endDate; // 종료 날짜
    private Double budget; // 출장 경비

    private ApproverDTO approver; // Approver 정보
    private List<BusinessTripScheduleRequestDTO> schedules; // 출장 일정
}