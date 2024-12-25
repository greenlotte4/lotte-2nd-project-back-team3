package BackAnt.service;

import BackAnt.dto.NotificationDTO;
import BackAnt.dto.RequestDTO.VacationRequestDTO;
import BackAnt.dto.approval.BusinessTripDTO;
import BackAnt.dto.approval.VacationDTO;
import BackAnt.entity.User;
import BackAnt.entity.approval.Approver;
import BackAnt.entity.approval.BusinessTrip;
import BackAnt.entity.approval.Vacation;
import BackAnt.repository.ApproverRepository;
import BackAnt.repository.BusinessTripRepository;
import BackAnt.repository.UserRepository;
import BackAnt.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class VacationService {
    private final VacationRepository vacationRepository;
    private final UserRepository userRepository;
    private final ApproverRepository approverRepository;
    private final ImageService imageService;
    private final NotificationService notificationService;
    private final ApprovalRequestService approvalRequestService;
    private final BusinessTripRepository businessTripRepository;
    // 휴가 신청 로직
    @Transactional
    public void createVacation(VacationRequestDTO requestDto, MultipartFile proofFile) throws Exception {
        String proofUrl = null;

        log.info("휴가 신청 시작");

        try {
            // 증빙 파일 업로드
            if (proofFile != null && !proofFile.isEmpty()) {
                proofUrl = imageService.uploadImage(proofFile, "vaction");
                log.info("증빙 파일 업로드 성공, URL: {}", proofUrl);
            }
        } catch (Exception e) {
            log.error("증빙 파일 업로드 중 오류 발생", e);
            throw e;
        }

        try {
            // Approver에 연결된 User를 조회하거나 생성
            User approverUser = userRepository.findById(requestDto.getApprover().getId())
                    .orElseThrow(() -> new RuntimeException("Approver User not found"));
            log.info("Approver User 조회 성공: {}", approverUser.getId());

            Approver approver = approverRepository.findByUser(approverUser)
                    .orElseGet(() -> {
                        Approver newApprover = Approver.builder()
                                .user(approverUser)
                                .status(requestDto.getApprover().getStatus())
                                .build();
                        approverRepository.save(newApprover);
                        log.info("새 Approver 저장 성공: {}", newApprover.getId());
                        return newApprover;
                    });

            // Vacation 엔티티 생성 및 저장
            Vacation vacation = Vacation.builder()
                    .userId(requestDto.getUserId())
                    .userName(requestDto.getUserName())
                    .title(requestDto.getTitle())
                    .department(requestDto.getDepartment())
                    .companyName(requestDto.getCompanyName())
                    .startDate(requestDto.getStartDate())
                    .endDate(requestDto.getEndDate())
                    .annualLeaveRequest(requestDto.getAnnualLeaveRequest())
                    .vacation_type(requestDto.getType())
                    .type("휴가신청")
                    .halfDay(requestDto.getHalfDay())
                    .approvalDate(LocalDate.now())
                    .proofUrl(proofUrl)
                    .approver(approver)
                    .status("대기") // 초기 상태
                    .build();

            log.info("Vacation 엔티티 생성 완료: {}", vacation);
            vacationRepository.save(vacation);
            log.info("Vacation 엔티티 저장 성공");
        } catch (Exception e) {
            log.error("Vacation 저장 중 오류 발생", e);
            throw e;
        }

        try {
            // WebSocket을 통한 실시간 알림 전송
            NotificationDTO notification = NotificationDTO.builder()
                    .targetType("USER")
                    .targetId(requestDto.getApprover().getId()) // Approver ID
                    .senderId(requestDto.getUserId())
                    .message(requestDto.getUserName() + "님이 휴가를 신청했습니다.")
                    .metadata(Map.of(
                            "url", "/antwork/admin/approval",
                            "type", "휴가신청",
                            "title", requestDto.getTitle()
                    ))
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();

            log.info("Notification 생성 완료: {}", notification);
            notificationService.createAndSendNotification(notification);
            log.info("Notification 전송 성공");
        } catch (Exception e) {
            log.error("Notification 전송 중 오류 발생", e);
            throw e;
        }

        log.info("휴가 신청 완료");
    }


    public List<Long> findVacationUser() {
        List<Vacation> vacations = vacationRepository.findByStatus("승인");
        List<BusinessTrip> businessTrips = businessTripRepository.findByStatus("승인");
        List<VacationDTO> vacationDTOs = new ArrayList<>();
        List<BusinessTripDTO> businessTripDTOs = new ArrayList<>();
        vacations.forEach(vacation -> {

            vacationDTOs.add(approvalRequestService.getApprovalDetails(vacation.getId()));
        });
        businessTrips.forEach(businessTrip -> {

            businessTripDTOs.add(approvalRequestService.getApprovalDetails(businessTrip.getId()));

        });

        LocalDate today = LocalDate.now();


        // 2. "휴가신청"에서 "연차"와 "반차" 나누기
        List<VacationDTO> annualLeaveRequests = vacationDTOs.stream()
                .filter(dto -> "연차".equals(dto.getVacationType()))
                .toList();

        List<VacationDTO> halfDayRequests = vacationDTOs.stream()
                .filter(dto -> "반차".equals(dto.getVacationType()))
                .toList();



        // 3. 오늘 날짜가 포함된 데이터 개수 세기
        long annualLeaveCount = annualLeaveRequests.stream()
                .filter(dto -> isDateInRange(dto.getStartDate(), dto.getEndDate(), today))
                .count();

        long halfDayCount = halfDayRequests.stream()
                .filter(dto -> isDateInRange(dto.getStartDate(), dto.getEndDate(), today))
                .count();

        long businessCount = businessTripDTOs.stream()
                .filter(dto -> isDateInRange(dto.getStartDate(), dto.getEndDate(), today))
                .count();


        List<Long> userList = new ArrayList<>();
        userList.add(businessCount);
        userList.add(annualLeaveCount);
        userList.add(halfDayCount);

        return userList;

    }

    private static boolean isDateInRange(LocalDate startDate, LocalDate endDate, LocalDate today) {
        return (startDate.isEqual(today) || startDate.isBefore(today)) &&
                (endDate.isEqual(today) || endDate.isAfter(today));
    }

}
