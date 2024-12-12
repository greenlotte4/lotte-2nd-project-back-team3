package BackAnt.dto.approval;

import BackAnt.entity.approval.Vacation;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class VacationDTO implements ApprovalRequestDTO {
    private Long id; // ApprovalRequest의 공통 필드
    private Long userId;
    private String userName;
    private String department;
    private String companyName;
    private LocalDate submissionDate;
    private LocalDate approvalDate;
    private String type; // 신청서 타입
    private String status; // "대기", "승인", "반려"
    private String title; // 휴가 제목
    private String vacationType; // 연차, 반차, 병가 등
    private LocalDate startDate; // 휴가 시작일
    private LocalDate endDate; // 휴가 종료일
    private double annualLeaveRequest; // 신청 연차
    private String proofUrl; // 증빙 파일 URL
    private String approverName; // 승인자 이름 추가

    // Vacation 엔티티에서 DTO로 변환하는 정적 메서드
    public static VacationDTO of(Vacation vacation) {
        return VacationDTO.builder()
                .id(vacation.getId())
                .userId(vacation.getUserId())
                .userName(vacation.getUserName())
                .department(vacation.getDepartment())
                .companyName(vacation.getCompanyName())
                .submissionDate(vacation.getSubmissionDate())
                .approvalDate(vacation.getApprovalDate())
                .type(vacation.getType())
                .status(vacation.getStatus())
                .title(vacation.getTitle())
                .vacationType(vacation.getVacation_type())
                .startDate(vacation.getStartDate())
                .endDate(vacation.getEndDate())
                .annualLeaveRequest(vacation.getAnnualLeaveRequest())
                .proofUrl(vacation.getProofUrl())
                .build();
    }
}
