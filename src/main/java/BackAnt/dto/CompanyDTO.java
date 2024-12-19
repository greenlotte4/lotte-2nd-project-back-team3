package BackAnt.dto;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/*
    날짜 : 2024/11/29
    이름 : 최준혁
    내용 : CompanyDTO 생성
*/

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {

    private int id;
    private String name;
    private String description;
    private LocalDate foundationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String address;
    private String businessNumber;
    private String logo;
    private int rate;
    private LocalDateTime createdAt;
}
