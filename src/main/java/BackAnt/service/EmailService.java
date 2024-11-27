package BackAnt.service;

import BackAnt.JWT.JwtProvider;
import BackAnt.entity.User;
import BackAnt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final ConcurrentHashMap<String, Boolean> tokenVerificationStatus = new ConcurrentHashMap<>();


    @Autowired
    public EmailService(JavaMailSender mailSender, JwtProvider jwtProvider, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    // 이메일 전송
    public void sendEmail(String to, String subject, String body) {
        // JWT 생성
        String token = jwtProvider.createEmailToken(to, 1); // 유효기간: 1일

        // 인증 링크 생성
        String verificationLink = "http://localhost:8080/api/email/verify?token=" + token;

        // 토큰 인증 상태 초기화
        tokenVerificationStatus.put(token, false); // 토큰 등록 (초기 상태: 인증되지 않음)

        // 이메일 본문
        String fullBody = body + "\n\n인증 링크: " + verificationLink;

        // 이메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(fullBody);
        mailSender.send(message);
    }


    // 이메일 인증 처리
    public String verifyEmail(String token) {
        // 토큰 검증 및 이메일 추출
        String email = jwtProvider.validateAndExtractEmail(token);

        // 토큰 상태 확인 및 업데이트
        if (!tokenVerificationStatus.containsKey(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        if (Boolean.TRUE.equals(tokenVerificationStatus.get(token))) {
            throw new IllegalArgumentException("이미 인증이 완료된 토큰입니다.");
        }

        // 인증 상태 업데이트
        tokenVerificationStatus.put(token, true);
        return email;
    }

    // 이메일 인증 상태 확인
    public boolean isTokenVerified(String token) {
        return tokenVerificationStatus.getOrDefault(token, false);
    }
}
