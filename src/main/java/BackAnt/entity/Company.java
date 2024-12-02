package BackAnt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
    private Long id; // 회사 고유 ID

    @Column(nullable = false, length = 255)
    private String name; // 회사 이름

    private String address; // 회사 주소
    private String phone;   // 회사 대표 전화번호
    private String logoUrl; // 회사 로고 URL

    @OneToMany(mappedBy = "company")
    private List<Department> departments; // 회사에 속한 부서들

    @OneToMany(mappedBy = "company")
    private List<User> users; // 회사에 속한 사용자들

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
