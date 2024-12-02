package BackAnt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 부서 고유 ID
    private String name; // 부서명

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // 소속 회사

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Department parent; // 상위 부서 (자기 참조)

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Department> children; // 하위 부서들

//    private String description; // 부서 설명 -- 필요하면 추가

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users; // 부서에 속한 사용자들

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
