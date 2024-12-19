package BackAnt.dto.calendar;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDTO {
    private int calendarId;
    private String name;
    private String user_id;
    private String view;
    private String color;

    private boolean share;
    private int no;
}
