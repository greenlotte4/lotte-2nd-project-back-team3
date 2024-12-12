package BackAnt.entity.approval;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "BusinessTripSchedule")
public class BusinessTripSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; // 방문 날짜
    private String company; // 방문 회사
    private String department; // 방문 부서
    private String contact; // 담당자 연락처
    private String note; // 비고

    @ManyToOne
    @JoinColumn(name = "business_trip_id")
    @JsonIgnore // 순환 참조 방지
    private BusinessTrip businessTrip; // 부모 엔티티
}
