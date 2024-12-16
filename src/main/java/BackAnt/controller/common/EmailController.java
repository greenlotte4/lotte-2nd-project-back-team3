package BackAnt.controller.common;

import BackAnt.dto.RequestDTO.EmailRequestDTO;
import BackAnt.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
public class EmailController {


    private final EmailService emailService;

    // 이메일 전송 요청
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody EmailRequestDTO request) {
        if(Objects.equals(request.getSubject(), "아이디 찾기")|| Objects.equals(request.getSubject(), "비밀번호 찾기")){
            try {
                log.info("asdfasdfasdfasdfsdf");
                int number = emailService.idCheck(request.getTo(), request.getSubject(), request.getBody());

                Map<String, String> response = new HashMap<>();
                response.put("message", "이메일이 성공적으로 전송되었습니다.");
                response.put("number", String.valueOf(number));
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.status(500).body(Map.of("error", "이메일 전송 실패: " + e.getMessage()));
            }
        }
        try {
            String token = emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());

            Map<String, String> response = new HashMap<>();
            response.put("message", "이메일이 성공적으로 전송되었습니다.");
            response.put("token", token); // 토큰 반환
            log.info("토큰" + token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "이메일 전송 실패: " + e.getMessage()));
        }
    }


    // 이메일 인증
    @GetMapping("/verify")
    public ResponseEntity<String> verifyAndCheckEmailAPI(@RequestParam("token") String token) {
        try {
            String email = emailService.verifyAndCheckEmail(token);
            log.info("로그 들어와서 인증 성공하니?");
            return ResponseEntity.ok("이메일 인증이 성공적으로 완료되었습니다! 이메일: " + email);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }

    // 이메일 인증 상태 확인
    @GetMapping("/check-verification")
    public ResponseEntity<Map<String, Boolean>> checkVerification(@RequestParam("token") String token) {
        boolean isVerified = emailService.isEmailVerified(token);
        Map<String, Boolean> response = new HashMap<>();
        response.put("verified", isVerified);
        return ResponseEntity.ok(response); // JSON 형식으로 인증 상태 반환
    }

}