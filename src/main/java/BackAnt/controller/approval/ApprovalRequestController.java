package BackAnt.controller.approval;

import BackAnt.dto.approval.ApprovalRequestDTO;
import BackAnt.entity.approval.ApprovalRequest;
import BackAnt.entity.approval.BusinessTrip;
import BackAnt.entity.approval.Vacation;
import BackAnt.service.ApprovalRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/approval-requests")
public class ApprovalRequestController {

    private final ApprovalRequestService approvalRequestService;

    // 관리자 결제요청 처리 서류 조회
    @GetMapping("/approver/{approverId}")
    public ResponseEntity<Map<String, Object>> getApprovalRequestsByApprover(
            @PathVariable Long approverId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ApprovalRequest> approvalRequests = approvalRequestService.getApprovalRequestsByApprover(approverId, status, type, pageable);

            // DTO 변환
            List<Map<String, Object>> requestData = approvalRequests.getContent().stream().map(request -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", request.getId());
                map.put("type", request.getType());
                map.put("title",
                        request instanceof BusinessTrip ? ((BusinessTrip) request).getTitle() :
                                request instanceof Vacation ? ((Vacation) request).getTitle() :
                                        null
                );
                map.put("status", request.getStatus());
                map.put("userName", request.getUserName());
                map.put("department", request.getDepartment());
                map.put("submissionDate", request.getSubmissionDate());
                map.put("approvalDate", request.getApprovalDate());
                return map;
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("data", requestData);
            response.put("currentPage", approvalRequests.getNumber());
            response.put("totalItems", approvalRequests.getTotalElements());
            response.put("totalPages", approvalRequests.getTotalPages());
            log.info(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("문서 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 특정 사용자의 문서 조회 API
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getApprovalRequests(
            @PathVariable Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ApprovalRequest> approvalRequests = approvalRequestService.getApprovalRequests(userId, status, type, pageable);

            // DTO 변환
            List<Map<String, Object>> requestData = approvalRequests.getContent().stream().map(request -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", request.getId());
                map.put("type", request.getType());
                map.put("title",
                        request instanceof BusinessTrip ? ((BusinessTrip) request).getTitle() :
                                request instanceof Vacation ? ((Vacation) request).getTitle() :
                                        null
                );
                map.put("status", request.getStatus());
                map.put("userName", request.getUserName());
                map.put("submissionDate", request.getSubmissionDate());
                map.put("approver", request.getApprover() != null ? request.getApprover().getUser().getName() : null); // Approver 이름
                map.put("approvalDate", request.getApprovalDate()); // 예: 결재일자를 따로 저장한 경우 추가 로직 필요
                return map;
            }).collect(Collectors.toList());


            Map<String, Object> response = new HashMap<>();
            response.put("data", requestData);
            response.put("currentPage", approvalRequests.getNumber());
            response.put("totalItems", approvalRequests.getTotalElements());
            response.put("totalPages", approvalRequests.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("문서 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 특정 문서 조회
    @GetMapping("/detail/{id}")
    public ResponseEntity<? extends ApprovalRequestDTO> getApprovalDetails(@PathVariable Long id) {

        ApprovalRequestDTO approvalDTO = approvalRequestService.getApprovalDetails(id);
        log.info("보자보자" + approvalDTO);
        return ResponseEntity.ok(approvalDTO);
    }

    // 문서 승인 및 반려
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateApprovalStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        try {
            approvalRequestService.updateApprovalStatus(id, status);
            return ResponseEntity.ok("결재 상태가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결재 상태 변경 중 오류 발생: " + e.getMessage());
        }
    }
}
