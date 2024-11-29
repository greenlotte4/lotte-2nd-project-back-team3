package BackAnt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
    날짜 : 2024/11/29
    이름 : 최준혁
    내용 : Invite 엔티티 생성
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
    private String email;
    private String companyName;
    private String token;
    private LocalDateTime expirationDate;

}
