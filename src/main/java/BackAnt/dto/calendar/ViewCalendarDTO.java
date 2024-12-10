package BackAnt.dto.calendar;

import jakarta.persistence.*;
import lombok.*;

/*
    날짜 : 2024/12/10
    이름 : 하정훈
    내용 : view 엔티티 생성
*/

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewCalendarDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 무결성을 위한 AI PK
    private int id;

    private int calendarId;
    private int userId;

}
