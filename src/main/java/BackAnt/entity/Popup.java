package BackAnt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
/*
    날 짜 : 2024/12/8(일)
    담당자 : 최준혁
    내 용 : Popup 를 위한 Entity 생성
*/

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Popup")
public class Popup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private boolean active;
    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private Long companyId; // 회사 ID 필드 추가
}
