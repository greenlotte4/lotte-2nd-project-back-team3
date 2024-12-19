package BackAnt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/*
    날짜 : 2024/11/29
    이름 : 최준혁
    내용 : Company 엔티티 생성
*/

@Getter
@Setter
@ToString
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

    private int rate; // 회사 요금제 ( 0 - 무료 | 1 - 유료  | 2 - 프로 )

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
