package BackAnt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/*
    날짜 : 2024/11/29
    이름 : 최준혁
    내용 : Company 엔티티 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private LocalDate foundationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String address;
    private String businessNumber;
    private String logo;
}
