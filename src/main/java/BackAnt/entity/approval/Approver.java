package BackAnt.entity.approval;

import BackAnt.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Approver")
public class Approver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // 외래 키로 user_id 사용
    private User user; // Approver는 특정 User와 연결됨

    private String status; // "대기", "승인", "반려"
}