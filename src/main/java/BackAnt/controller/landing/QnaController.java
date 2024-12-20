package BackAnt.controller.landing;


import BackAnt.dto.landing.QnaAnswerRequestDTO;
import BackAnt.dto.landing.QnaRequestDTO;
import BackAnt.dto.landing.QnaResponseDTO;
import BackAnt.dto.landing.QnaSearchDTO;
import BackAnt.service.landing.QnaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/qna")
public class QnaController {

    private final QnaService qnaService;

    // 문의 등록
    @PostMapping("/save")
        public ResponseEntity<QnaResponseDTO> saveQna(@RequestBody QnaRequestDTO qnaDTO){

            log.info("qna Insert data :  "+qnaDTO);
            QnaResponseDTO savedQna = qnaService.insertQna(qnaDTO);
          return ResponseEntity.status(HttpStatus.CREATED)
                  .body(savedQna);
    }

    // 문의 조회
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchQna(@RequestBody QnaSearchDTO qnaSearchDTO) {
        Map<String, Object> response = new HashMap<>();

        // 관리자 로그인 시 모든 문의 조회
        if(qnaSearchDTO.getEmail().equals("admin") && qnaSearchDTO.getTempPassword().equals("1234")) {
            response.put("isAdmin", true);
            response.put("inquiries", qnaService.selectAllForAdmin());
            return ResponseEntity.ok(response);
        }

        // 일반 사용자 로그인 시
        List<QnaResponseDTO> qnas = qnaService.selectListByEmail(qnaSearchDTO.getEmail(), qnaSearchDTO.getTempPassword());
        if (qnas == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        response.put("isAdmin", false);
        response.put("inquiries", qnas);
        return ResponseEntity.ok(response);
    }

    // 답변 추가
    @PostMapping("/answer/{id}")
    public ResponseEntity<QnaResponseDTO> updateAnswer(
            @PathVariable Long id,
            @RequestBody QnaAnswerRequestDTO request
    ) {
        log.info("답변 작성 컨트롤러 RequestData:"+request);
        QnaResponseDTO updatedQna = qnaService.updateAnswer(id, request.getAnswer());
        return ResponseEntity.status(HttpStatus.OK).body(updatedQna);
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<QnaResponseDTO> modifyQna(@PathVariable Long id,  @RequestBody QnaRequestDTO qnaDTO){
        log.info("modify Qna Request Data "+qnaDTO);
        QnaResponseDTO modifiedQna = qnaService.modifyQna(id, qnaDTO);

        return ResponseEntity.status(HttpStatus.OK).body(modifiedQna);
    }

}
