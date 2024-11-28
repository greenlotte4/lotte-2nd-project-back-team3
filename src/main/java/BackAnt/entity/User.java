package BackAnt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 무결성을 위한 AI PK
    private int id;

    private String uid; // 고유 ID

    private String username; // 사용자 이름
    private String email; // 이메일
    private String password; // 비밀번호 (암호화된 상태로 저장)
    private String role = "USER"; // 역할 (기본값: USER)
//    private String permissions; // 권한 --> 추후 필요할 경우 추가
    private String department; // 부서명
    private String position; // 직위
    private String phoneNumber; // 연락처
    private String profilePicture; // 프로필 사진 URL
    private LocalDateTime lastLoginAt; // 마지막 로그인 시간
    private Boolean isActive = true; // 계정 활성화 여부 (기본값: true)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간
    private LocalDateTime updatedAt = LocalDateTime.now(); // 업데이트 시간

    // @PreUpdate -> 엔티티가 업데이트 되기 직전에 실행
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
