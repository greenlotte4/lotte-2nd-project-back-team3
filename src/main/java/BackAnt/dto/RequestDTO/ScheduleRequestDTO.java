package BackAnt.dto.RequestDTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequestDTO {
    private LocalDate date;
    private String company;
    private String department;
    private String contact;
    private String note;
}