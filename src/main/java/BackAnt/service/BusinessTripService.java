package BackAnt.service;

import BackAnt.dto.NotificationDTO;
import BackAnt.dto.RequestDTO.BusinessTripRequestDTO;
import BackAnt.entity.User;
import BackAnt.entity.approval.Approver;
import BackAnt.entity.approval.BusinessTrip;
import BackAnt.entity.approval.BusinessTripSchedule;
import BackAnt.repository.ApproverRepository;
import BackAnt.repository.BusinessTripRepository;
import BackAnt.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class BusinessTripService {
    private final BusinessTripRepository businessTripRepository;
    private final ApproverRepository approverRepository;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 출장신청
    @Transactional
    public void createBusinessTrip(BusinessTripRequestDTO requestDto) {

        log.info("입성");

        // Approver에 연결된 User를 조회하거나 생성
        User approverUser = userRepository.findById(requestDto.getApprover().getId())
                .orElseThrow(() -> new RuntimeException("Approver User not found"));

        Approver approver = approverRepository.findByUser(approverUser)
                .orElseGet(() -> approverRepository.save(
                        Approver.builder()
                                .user(approverUser)
                                .status(requestDto.getApprover().getStatus())
                                .build()
                ));

        log.info("입성2");

        // BusinessTrip 객체 생성
        BusinessTrip businessTrip = BusinessTrip.builder()
                .userId(requestDto.getUserId())
                .userName(requestDto.getUserName())
                .department(requestDto.getDepartment())
                .companyName(requestDto.getCompanyName())
                .title(requestDto.getTitle())
                .organization(requestDto.getOrganization())
                .purpose(requestDto.getPurpose())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .budget(requestDto.getBudget())
                .approvalDate(LocalDate.now())
                .status("대기")
                .type("출장신청")
                .approver(approver) // 영속화된 Approver 사용
                .build();

        // BusinessTripSchedule 리스트 생성
        List<BusinessTripSchedule> schedules = requestDto.getSchedules().stream()
                .map(schedule -> BusinessTripSchedule.builder()
                        .date(schedule.getDate())
                        .company(schedule.getCompany())
                        .department(schedule.getDepartment())
                        .contact(schedule.getContact())
                        .note(schedule.getNote())
                        .businessTrip(businessTrip)
                        .build())
                .collect(Collectors.toList());

        businessTrip.setSchedules(schedules);

        log.info("입성3");

        // BusinessTrip 저장
        businessTripRepository.save(businessTrip);

        // WebSocket을 통한 실시간 알림 전송
        NotificationDTO notification = NotificationDTO.builder()
                .targetType("USER")
                .targetId(approver.getUser().getId()) // Approver ID
                .senderId(requestDto.getUserId())
                .message(requestDto.getUserName() + "님이 출장을 신청했습니다.")
                .metadata(Map.of(
                        "url", "/antwork/admin/approval",
                        "type", "출장신청",
                        "title", requestDto.getTitle()
                ))
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
        notificationService.createAndSendNotification(notification);
    }

}
