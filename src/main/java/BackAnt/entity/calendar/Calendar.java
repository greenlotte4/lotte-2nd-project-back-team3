package BackAnt.entity.calendar;

import BackAnt.entity.User;
import jakarta.persistence.*;
import lombok.*;

/*
    날짜 : 2024/12/02
    이름 : 하정훈
    내용 : Calendar 엔티티 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Calendar")
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 무결성을 위한 AI PK
    private int calendarId;

    private String name;
    private String view;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "uid", nullable = false)  // 외래 키 설정
    private User user;
}
