package BackAnt.dto.approval;

import BackAnt.dto.approval.ApprovalRequestDTO;
import BackAnt.dto.approval.ScheduleDTO;
import BackAnt.entity.approval.BusinessTrip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@ToString
public class BusinessTripDTO implements ApprovalRequestDTO {
    private Long id;
    private String userName;
    private String department;
    private String companyName;
    private String approverName; // 승인자 이름 추가
    private String type;
    private String status;
    private String title;
    private String organization;
    private String purpose;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double budget;
    private LocalDate submissionDate; // 제출일자 추가
    private LocalDate approvalDate;   // 승인일자 추가
    private List<ScheduleDTO> schedules;

    public static BusinessTripDTO of(BusinessTrip businessTrip) {
        return new BusinessTripDTO(
                businessTrip.getId(),
                businessTrip.getUserName(),
                businessTrip.getDepartment(),
                businessTrip.getCompanyName(),
                null, // approverName은 후에 설정
                businessTrip.getType(),
                businessTrip.getStatus(),
                businessTrip.getTitle(),
                businessTrip.getOrganization(),
                businessTrip.getPurpose(),
                businessTrip.getStartDate(),
                businessTrip.getEndDate(),
                businessTrip.getBudget(),
                businessTrip.getSubmissionDate(), // ApprovalRequest의 필드 사용
                businessTrip.getApprovalDate(),   // ApprovalRequest의 필드 사용
                businessTrip.getSchedules().stream()
                        .map(ScheduleDTO::of)
                        .toList()
        );
    }
}
