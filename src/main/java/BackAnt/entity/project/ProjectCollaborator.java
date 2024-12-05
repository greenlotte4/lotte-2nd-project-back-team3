package BackAnt.entity.project;

import BackAnt.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/*
    날짜 : 2024/12/2
    이름 : 강은경
    내용 : ProjectCollaborator 엔티티 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "ProjectCollaborator")
public class ProjectCollaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // 소속된 프로젝트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 소속된 사용자

    @CreationTimestamp
    private LocalDateTime invitedAt; // 프로젝트 초대 날짜

    private boolean isOwner; //  최고관리자인지 아닌지 (프로젝트 생성할때 만든사람이 최고관리자가 됨)

    private int type; // 프로젝트 권한 (ADMIN, WRITE, READ)
 }
