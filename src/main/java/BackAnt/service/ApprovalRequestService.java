package BackAnt.service;

import BackAnt.dto.approval.ApprovalRequestDTO;
import BackAnt.dto.approval.BusinessTripDTO;
import BackAnt.dto.approval.VacationDTO;
import BackAnt.entity.approval.ApprovalRequest;
import BackAnt.entity.approval.BusinessTrip;
import BackAnt.repository.ApprovalRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ApprovalRequestService {
    private final ApprovalRequestRepository approvalRequestRepository;

    // 모든 문서 조회 (+본인 아이디 기준)
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

        if (approvalRequest instanceof BusinessTrip) {
            return (T) BusinessTripDTO.of((BusinessTrip) approvalRequest);
        }
//         else if (approvalRequest instanceof Vacation) {
//            return (T) VacationDTO.of((Vacation) approvalRequest);
//        }

        throw new RuntimeException("알 수 없는 문서 타입입니다.");
    }



}
