package BackAnt.dto.calendar;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> internalAttendees;
    private List<String> externalAttendees;
    private String location;
    private LocalDateTime start;
    private LocalDateTime end;
    private String uid;

    private String color;
    private String action;
}
