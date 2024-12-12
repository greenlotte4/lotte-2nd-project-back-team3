package BackAnt.controller.approval;

import BackAnt.dto.RequestDTO.BusinessTripRequestDTO;
import BackAnt.service.BusinessTripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/businesstrips")
public class BusinessTripController {
    private final BusinessTripService businessTripService;

    // 출장 신청
    @PostMapping
    public ResponseEntity<String> createBusinessTrip(@RequestBody BusinessTripRequestDTO requestDto) {
        try {
            log.info("요청데이터" + requestDto.toString());
            businessTripService.createBusinessTrip(requestDto);
            return ResponseEntity.ok("출장 신청이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("출장 신청 저장 중 오류가 발생했습니다.");
        }
    }
}
