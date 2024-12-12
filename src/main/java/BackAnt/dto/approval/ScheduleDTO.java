package BackAnt.dto.approval;

import BackAnt.entity.approval.BusinessTripSchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@ToString
public class ScheduleDTO {
    private LocalDate date;
    private String company;
    private String department;
    private String contact;
    private String note;

    public static ScheduleDTO of(BusinessTripSchedule schedule) {
        return new ScheduleDTO(
                schedule.getDate(),
                schedule.getCompany(),
                schedule.getDepartment(),
                schedule.getContact(),
                schedule.getNote()
        );
    }
}
