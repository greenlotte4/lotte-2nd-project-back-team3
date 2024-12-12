package BackAnt.service;

import BackAnt.dto.RequestDTO.VacationRequestDTO;
import BackAnt.entity.User;
import BackAnt.entity.approval.Approver;
import BackAnt.entity.approval.Vacation;
import BackAnt.repository.ApproverRepository;
import BackAnt.repository.UserRepository;
import BackAnt.repository.VactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VacationService {
    private final VactionRepository vactionRepository;
    private final UserRepository userRepository;
    private final ApproverRepository approverRepository;
    private final ImageService imageService;

    // 휴가 신청 로직
    @Transactional
    public void createVacation(VacationRequestDTO requestDto, MultipartFile proofFile) throws Exception {
        String proofUrl = null;

        // 증빙 파일 업로드
        if (proofFile != null && !proofFile.isEmpty()) {
            proofUrl = imageService.uploadImage(proofFile, "vaction");
        }

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


        // Vaction 엔티티 생성
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

        vactionRepository.save(vacation);
    }
}
