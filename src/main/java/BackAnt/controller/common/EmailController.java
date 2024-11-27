package BackAnt.controller.common;

import BackAnt.dto.EmailRequestDTO;
import BackAnt.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
public class EmailController {


    private final EmailService emailService;

    // 이메일 전송 요청
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO request) {
        try {
            emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
            return ResponseEntity.ok("이메일이 성공적으로 전송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("이메일 전송 실패: " + e.getMessage());
        }
    }

    // 이메일 인증 요청
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            String email = emailService.verifyEmail(token);
            return ResponseEntity.ok("이메일 인증이 성공적으로 완료되었습니다! 이메일: " + email);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }

    @GetMapping("/check-verification")
    public ResponseEntity<Boolean> isEmailVerified(@RequestParam("token") String token) {
        log.info("들어오니? 폴링?");
        boolean isVerified = emailService.isTokenVerified(token);
        return ResponseEntity.ok(isVerified);
    }

}