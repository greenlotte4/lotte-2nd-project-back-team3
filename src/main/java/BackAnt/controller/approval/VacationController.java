package BackAnt.controller.approval;

import BackAnt.dto.RequestDTO.VacationRequestDTO;
import BackAnt.service.VacationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequestMapping("/api/vacation")
@RequiredArgsConstructor
public class VacationController {

    private final VacationService vacationService;
    private final ObjectMapper objectMapper;

    @PostMapping("/request")
    public ResponseEntity<?> createVacationRequest(
            @RequestPart("formData") VacationRequestDTO requestDto, // JSON 데이터
            @RequestPart(value = "proofFile", required = false) MultipartFile proofFile // 파일 데이터
    ) {
        try {
            log.info("데이터 구조 확인" + requestDto.toString());
            // 휴가 신청 처리
            vacationService.createVacation(requestDto, proofFile);
            log.info("컨트롤러 나왼?");
            return ResponseEntity.ok("휴가 신청이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("휴가 신청 중 오류 발생: " + e.getMessage());
        }
    }

}
