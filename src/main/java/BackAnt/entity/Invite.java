package BackAnt.entity;

import BackAnt.entity.enums.Role;
import BackAnt.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
    날짜 : 2024/11/29
    이름 : 최준혁
    내용 : 멤버 초대 관리를 위한 Invite 엔티티 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Invite")
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; // 초대받은 사용자의 이메일
    private String name; // 초대받은 사용자의 이름
    private String position; // 직위
    private String phoneNumber; // 연락처

    private String inviteToken; // 초대 토큰
    private LocalDateTime expiry; // 토큰 만료 시간

    @Enumerated(EnumType.STRING)
    private Status status = Status.INVITE; // 초대 상태 (기본값: INVITE)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department; // 소속 부서

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER; // 초대받은 사용자의 역할

    private String note; // 관리자가 남긴 초대 메모

    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
