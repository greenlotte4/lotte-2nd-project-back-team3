package BackAnt.entity;

import BackAnt.entity.enums.Role;
import BackAnt.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
    날짜 : 2024/11/29
    이름 : 최준혁
    내용 : User 엔티티 생성
*/

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 사용자 이름

    @Column(nullable = false, unique = true)
    private String uid; // 아이디

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    private String nick; // 닉네임

    @Column(nullable = false)
    private String password; // 비밀번호 (암호화된 상태로 저장)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER; // 역할 (기본값: USER)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE; // 계정 상태 (기본값: ACTIVE)

    private String position; // 직위
    private String phoneNumber; // 연락처
    private String profileImageUrl; // 프로필 사진 URL
    private LocalDate joinDate; // 입사일
    private String calendarLanguage;

    private LocalDateTime lastLoginAt; // 마지막 로그인 시간

    @Builder.Default
    private Boolean isActive = true; // 계정 활성화 여부 (기본값: true)

    private double annualLeaveTotal; // 총 부여된 연차 (일 단위)
    private double annualLeaveUsed;  // 사용한 연차 (일 단위)

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간
    private LocalDateTime updatedAt = LocalDateTime.now(); // 업데이트 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @ToString.Exclude
    private Department department; // 소속 부서

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "company_id", nullable = false)
    @ToString.Exclude
    private Company company; // 소속 회사

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public double getRemainingLeave() {
        return annualLeaveTotal - annualLeaveUsed;
    }

    public void updateName(String newName){
        this.name = newName;
    }
    public void updateEmail(String newEmail){
        this.email = newEmail;
    }
    public void updatePhoneNumber(String newPhoneNumber){
        this.phoneNumber = newPhoneNumber;
    }
    public void updateProfileImageUrl(String newProfileImageUrl){
        this.profileImageUrl = newProfileImageUrl;
    }
    public void updateCalendarLanguage(String newCalendarLanguage){
        this.calendarLanguage = newCalendarLanguage;
    }
}
