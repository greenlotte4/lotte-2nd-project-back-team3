package BackAnt.service;

import BackAnt.dto.approval.ApprovalRequestDTO;
import BackAnt.dto.approval.BusinessTripDTO;
import BackAnt.dto.approval.VacationDTO;
import BackAnt.entity.approval.ApprovalRequest;
import BackAnt.entity.approval.BusinessTrip;
import BackAnt.entity.approval.Vacation;
import BackAnt.repository.ApprovalRequestRepository;
import BackAnt.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ApprovalRequestService {
    private final ApprovalRequestRepository approvalRequestRepository;
    private final DepartmentRepository departmentRepository;

    // 모든 문서 조회 (Admin)
    public Page<ApprovalRequest> getApprovalRequestsByApprover(Long approverId, String status, String type, Pageable pageable) {
        boolean isStatusAll = status == null || status.isEmpty() || status.equals("전체");
        boolean isTypeAll = type == null || type.isEmpty() || type.equals("전체");

        Page<ApprovalRequest> requests;

        if (isStatusAll && isTypeAll) {
            requests = approvalRequestRepository.findAllByApproverId(approverId, pageable);
        } else if (isTypeAll) {
            requests = approvalRequestRepository.findAllByApproverIdAndStatus(approverId, status, pageable);
        } else if (isStatusAll) {
            requests = approvalRequestRepository.findAllByApproverIdAndType(approverId, type, pageable);
        } else {
            requests = approvalRequestRepository.findAllByApproverIdAndFilters(approverId, status, type, pageable);
        }

        // 부서 이름 설정
        requests.getContent().forEach(request -> {
            if (request.getDepartment() != null) {
                departmentRepository.findById(Long.valueOf(request.getDepartment()))
                        .ifPresent(department -> request.setDepartment(department.getName()));
            }
        });

        return requests;
    }


    // 모든 문서 조회 (User)
    public Page<ApprovalRequest> getApprovalRequests(Long userId, String status, String type, Pageable pageable) {
        log.info("입성?");

        boolean isStatusAll = status == null || status.isEmpty() || status.equals("전체");
        boolean isTypeAll = type == null || type.isEmpty() || type.equals("전체");

        if (isStatusAll && isTypeAll) {
            log.info("입성2?");
            log.info("페이저블 검사"+pageable.toString());
            log.info("뭐가 들어오노" + approvalRequestRepository.findAllByUserId(userId, pageable).getContent().toString());
            return approvalRequestRepository.findAllByUserId(userId, pageable);
        }

        if (isTypeAll) {
            log.info("입성3?");
            return approvalRequestRepository.findAllByUserIdAndStatus(userId, status, pageable);
        }

        if (isStatusAll) {
            log.info("입성4?");
            return approvalRequestRepository.findAllByUserIdAndType(userId, type, pageable);
        }

        log.info("입성5?");
        return approvalRequestRepository.findAllByUserIdAndFilters(userId, status, type, pageable);
    }

    // 특정 문서 상세 조회
    @Transactional
    @SuppressWarnings("unchecked")
    public <T extends ApprovalRequestDTO> T getApprovalDetails(Long id) {
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문서를 찾을 수 없습니다. ID: " + id));

        String approverName = approvalRequest.getApprover() != null
                ? approvalRequest.getApprover().getUser().getName()
                : null;

        if (approvalRequest instanceof BusinessTrip) {
            BusinessTripDTO businessTripDTO = BusinessTripDTO.of((BusinessTrip) approvalRequest);
            businessTripDTO.setApproverName(approverName);
            return (T) businessTripDTO;
        } else if (approvalRequest instanceof Vacation) {
            VacationDTO vacationDTO = VacationDTO.of((Vacation) approvalRequest);
            vacationDTO.setApproverName(approverName);
            return (T) vacationDTO;
        }

        throw new RuntimeException("알 수 없는 문서 타입입니다.");
    }

    // 문서 승인 및 반려
    @Transactional
    public void updateApprovalStatus(Long id, String status) {
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결재 요청을 찾을 수 없습니다. ID: " + id));

        if (!status.equals("승인") && !status.equals("반려")) {
            throw new IllegalArgumentException("유효하지 않은 상태입니다. '승인' 또는 '반려'만 허용됩니다.");
        }

        approvalRequest.setStatus(status);

        if(status.equals("승인")){
            approvalRequest.setSubmissionDate(LocalDate.now());
        }
        approvalRequestRepository.save(approvalRequest);
    }


}
