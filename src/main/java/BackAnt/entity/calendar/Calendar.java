package BackAnt.entity.calendar;

import BackAnt.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/*
    날짜 : 2024/12/02
    이름 : 하정훈
    내용 : Calendar 엔티티 생성
*/

@Getter
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

    private String color;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "uid", nullable = false)  // 외래 키 설정
    private User user;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ViewCalendar> viewCalendars = new ArrayList<>();

    public void update (String name, String color){
        this.name = name;
        this.color = color;
    }

    public void updateName (String name){
        this.name = name;
    }



}
