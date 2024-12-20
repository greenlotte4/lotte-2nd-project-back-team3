package BackAnt.entity.landing;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "qna")
@Entity
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName; // 회사명
    private String businessType; // 업종
    private String name; // 이름
    private String contactNumber; // 연락처
    private String email; // 이메일
    private String tempPassword; // 임시 비밀번호
    private String answer;

    private String inquiryDetails; // 문의사항

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime answeredAt;

}