package BackAnt.entity.project;

import BackAnt.entity.enums.AttributeType;
import jakarta.persistence.*;
import lombok.*;
/*
    날짜 : 2024/12/22 (일)
    이름 : 강은경
    내용 : ProjectTaskAttribute 엔티티 생성(우선순위, 크기 속성을 정하기 위한 엔티티)
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "ProjectTaskAttribute")
public class ProjectTaskAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 이름 (낮음, 보통, 높음 또는 S, M, L)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttributeType type; // 속성 유형

}
