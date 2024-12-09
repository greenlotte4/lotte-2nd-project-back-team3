package BackAnt.entity.calendar;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
    날짜 : 2024/12/02
    이름 : 하정훈
    내용 : Schedule 엔티티 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 무결성을 위한 AI PK
    private int id;

    private String title;
    private String content;
    private String internalAttendees;
    private String externalAttendees;
    private String location;
    private LocalDateTime start;
    private LocalDateTime end;



    @ManyToOne
    @JoinColumn(name = "calendarId", nullable = false)  // 외래 키 설정
    private Calendar calendar;

    public void updateTime(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public void updateAttendees(String internalAttendees, String externalAttendees) {
        this.internalAttendees = internalAttendees;
        this.externalAttendees = externalAttendees;
    }

}
