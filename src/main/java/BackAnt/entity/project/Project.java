package BackAnt.entity.project;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/*
    날짜 : 2024/12/2
    이름 : 강은경
    내용 : Project 엔티티 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String projectName;    // 프로젝트 이름

    @Column(columnDefinition = "int default 0")
    private int status = 0; // 0:진행중, 1:완료

    @CreationTimestamp
    private LocalDateTime createdAt; // 프로젝트 생성 날짜

}
