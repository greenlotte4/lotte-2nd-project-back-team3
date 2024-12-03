package BackAnt.dto.calendar;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {

    private int id;
    private int calendarId;
    private String title;
    private String content;
    private String internalAttendees;
    private String externalAttendees;
    private String location;
    private LocalDateTime start;
    private LocalDateTime end;



}
