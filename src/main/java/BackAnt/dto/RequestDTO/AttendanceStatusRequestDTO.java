package BackAnt.dto.RequestDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceStatusRequestDTO {
    private String status;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}
