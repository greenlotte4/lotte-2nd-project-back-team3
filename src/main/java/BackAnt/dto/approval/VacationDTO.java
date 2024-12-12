package BackAnt.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@ToString
public class VacationDTO implements ApprovalRequestDTO {
    private Long id;
    private String userName;
    private String department;
    private String companyName;
    private String type;
    private String status;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int remainingDays;
//
//    public static VacationDTO of(Vacation vacation) {
//        return new VacationDTO(
//                vacation.getId(),
//                vacation.getUserName(),
//                vacation.getDepartment(),
//                vacation.getCompanyName(),
//                vacation.getType(),
//                vacation.getStatus(),
//                vacation.getLeaveType(),
//                vacation.getStartDate(),
//                vacation.getEndDate(),
//                vacation.getRemainingDays()
//        );
//    }
}
