package BackAnt.entity.approval;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "Vacation")
public class Vacation extends ApprovalRequest {
    
    private String title; // 제목
    private String vacation_type; // 연차, 반차, 병가, 기타 여부
    private LocalDate startDate; // 휴가 시작일
    private LocalDate endDate; // 휴가 종료일
    private double annualLeaveRequest;  // 신청 연차
    private String proofUrl; // 증빙파일 URL
    private String halfDay; // 반차 여부 (오전/오후)
}
