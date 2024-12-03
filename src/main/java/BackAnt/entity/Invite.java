package BackAnt.entity;

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

    private String inviteToken; // 초대 토큰
    private LocalDateTime expiry; // 토큰 만료 시간

    @Enumerated(EnumType.STRING)
    private Status status; // INVITED, EXPIRED 등

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 초대 대상 유저

    private LocalDateTime createdAt = LocalDateTime.now();

}
