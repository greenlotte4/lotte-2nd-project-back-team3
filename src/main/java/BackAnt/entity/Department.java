package BackAnt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/*
    날 짜 : 2024/12/5(목)
    담당자 : 최준혁
    내 용 : Department 를 위한 Entity 생성
*/

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 부서 고유 ID

    @Column(nullable = false, unique = true, length = 100)
    private String name; // 부서명

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "company_id", nullable = false)
    @JsonBackReference // 순환 참조 방지
    private Company company; // 소속 회사

    @OneToMany(mappedBy = "department")
    private List<User> users = new ArrayList<>(); // 부서에 속한 사용자들

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}