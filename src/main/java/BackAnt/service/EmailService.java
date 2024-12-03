package BackAnt.service;

import BackAnt.JWT.JwtProvider;
import BackAnt.entity.User;
import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    // 이메일 기반 인증 상태 관리
    private final ConcurrentHashMap<String, Boolean> emailVerificationStatus = new ConcurrentHashMap<>();

    // YML에서 읽어온 프론트엔드 URL
    @Value("${frontend.url}")
    private String frontendUrl;

    // 공통 이메일 전송 로직
    private void sendEmailMessage(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("Email sent to: " + to);
    }

    // 초대 이메일 전송
    public void sendInviteEmail(String email, String inviteToken) {
        String inviteLink = frontendUrl + "/register?token=" + inviteToken;
        String body = "멤버 초대를 받았습니다.\n\n초대 링크:\n" + inviteLink;

        sendEmailMessage(email, "멤버 초대", body);
    }

    // 이메일 인증 요청 전송
    public String sendEmail(String to, String subject, String body) {
        // JWT 생성
        String token = jwtProvider.createEmailToken(to, 1); // 유효기간: 1일

        // 인증 링크 생성
        String verificationLink = frontendUrl + "/email-verification?token=" + token;

        // 초기 인증 상태 저장
        emailVerificationStatus.put(to, false); // 이메일 초기 상태: 미인증

        // 이메일 본문
        String fullBody = body + "\n\n인증 링크:\n" + verificationLink;

        // 이메일 전송
        sendEmailMessage(to, subject, fullBody);

        // 토큰 반환
        return token;
    }

    // 이메일 인증 처리
    public String verifyAndCheckEmail(String token) {
        String email = jwtProvider.validateAndExtractEmail(token);

        if (Boolean.TRUE.equals(emailVerificationStatus.get(email))) {
            throw new IllegalArgumentException("이미 인증이 완료된 이메일입니다.");
        }

        emailVerificationStatus.put(email, true);

        return email;
    }

    // 이메일 인증 상태 확인
    public boolean isEmailVerified(String token) {
        try {
            String email = jwtProvider.validateAndExtractEmail(token);
            return emailVerificationStatus.getOrDefault(email, false);
        } catch (Exception e) {
            System.err.println("토큰 검증 실패: " + e.getMessage());
            return false;
        }
    }
}
