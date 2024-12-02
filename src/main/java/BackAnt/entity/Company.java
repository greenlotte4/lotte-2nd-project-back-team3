package BackAnt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Department> departments = new ArrayList<>(); // 회사에 속한 부서들

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<User> users = new ArrayList<>(); // 회사에 속한 사용자들

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
