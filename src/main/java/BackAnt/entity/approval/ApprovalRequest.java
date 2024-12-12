package BackAnt.entity.approval;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED) // 조인 전략 사용
@Table(name = "ApprovalRequest")
public abstract class ApprovalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String userName;
    private String department;
    private String companyName;
    private LocalDate submissionDate;
    private LocalDate approvalDate;

    private String type; // 신청서 타입: "출장", "휴가", "여비" 등

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private Approver approver; // 승인자 정보

    private String status; // "대기", "승인", "반려"
}
