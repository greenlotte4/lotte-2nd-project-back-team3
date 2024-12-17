package BackAnt.dto.RequestDTO;

import BackAnt.dto.approval.ApproverDTO;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationRequestDTO {
    private Long userId;
    private String userName;
    private String department;
    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private double annualLeaveRequest;
    private String type; // 연차, 반차, 병가 등
    private String title; // 휴가 제목
    private String note; // 추가 메모
    private ApproverDTO approver; // 승인자 정보
    private String halfDay; // 반차 여부 (오전/오후)
    private String proofUrl; // 증빙 파일 URL
}