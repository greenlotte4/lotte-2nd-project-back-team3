package BackAnt.entity.project;

import jakarta.persistence.*;
import lombok.*;

/*
    날짜 : 2024/12/2
    이름 : 강은경
    내용 : ProjectState 엔티티 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "ProjectState")
public class ProjectState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 상태 이름 (ex todo, ready, done 등등)

    private String description; // 상태 설명

    private String color; // 상태 색상 (HEX 값)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // 상태가 속한 프로젝트



}
