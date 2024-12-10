package BackAnt.entity.calendar;

import BackAnt.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
    날짜 : 2024/12/02
    이름 : 하정훈
    내용 : Schedule 엔티티 생성
*/

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "ViewCalendar")
public class ViewCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 무결성을 위한 AI PK
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendarId", nullable = false)  // 외래 키 설정
    private Calendar calendar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)  // 외래 키 설정
    private User user;

}
