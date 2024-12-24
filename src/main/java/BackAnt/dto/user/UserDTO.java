package BackAnt.dto.user;

import BackAnt.entity.enums.Role;
import BackAnt.entity.enums.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id; // 사용자 고유 ID
    private String name; // 사용자 이름
    private String uid; // 사용자 ID (고유)
    private String email; // 이메일
    private String password; // 비밀번호 (암호화 상태로 저장)
    private Role role; // 사용자 역할 (USER, ADMIN 등)
    private Status status; // 계정 상태 (ACTIVE, INVITED 등)
    private String position; // 직위
    private String phoneNumber; // 연락처
    private String profileImageUrl; // 프로필 이미지 URL
    private LocalDateTime lastLoginAt; // 마지막 로그인 시간
    private Boolean isActive; // 계정 활성화 여부
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 업데이트 시간

    private Long companyId; // 소속 회사 ID
    private Long departmentId; // 소속 부서 ID
    private String departmentName; // 소속 부서 ID
    private String calendarLanguage; // 캘린더 언어설정
}
