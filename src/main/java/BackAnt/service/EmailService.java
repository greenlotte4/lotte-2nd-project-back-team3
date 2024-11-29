package BackAnt.service;

import BackAnt.JWT.JwtProvider;
import BackAnt.entity.User;
import BackAnt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/*
    날짜 : 2024/11/29
    이름 : 최준혁
    내용 : 이메일 서비스 생성
*/

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    // 이메일 기반 인증 상태 관리
    private final ConcurrentHashMap<String, Boolean> emailVerificationStatus = new ConcurrentHashMap<>();

    @Autowired
    public EmailService(JavaMailSender mailSender, JwtProvider jwtProvider, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    // 이메일 전송 (인증)
    public String sendEmail(String to, String subject, String body) {
        // JWT 생성
        String token = jwtProvider.createEmailToken(to, 1); // 유효기간: 1일

        // 인증 링크 생성
        String verificationLink = "http://localhost:5173/email-verification?token=" + token;

        // 초기 인증 상태 저장
        emailVerificationStatus.put(to, false); // 이메일 초기 상태: 미인증

        // 이메일 본문
        String fullBody = body + "\n\n인증 링크:\n" + verificationLink;

        // 이메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(fullBody);
        mailSender.send(message);

        // 로깅
        System.out.println("Email sent to: " + to);
        System.out.println("Verification link: " + verificationLink);

        // 토큰 반환
        return token; // 생성된 토큰 반환
    }


    // 이메일 인증 처리
    public String verifyAndCheckEmail(String token) {
        // JWT 검증 및 이메일 추출
        String email = jwtProvider.validateAndExtractEmail(token);

        // 이미 인증된 이메일인지 확인
        if (Boolean.TRUE.equals(emailVerificationStatus.get(email))) {
            throw new IllegalArgumentException("이미 인증이 완료된 이메일입니다.");
        }

        // 인증 상태 업데이트
        emailVerificationStatus.put(email, true);

        // 인증된 이메일 반환
        return email;
    }

    // 이메일 인증 상태 확인
    public boolean isEmailVerified(String token) {
        try {
            // JWT 검증 및 이메일 추출
            String email = jwtProvider.validateAndExtractEmail(token); // 토큰에서 이메일 추출

            // 이메일 인증 상태 반환
            return emailVerificationStatus.getOrDefault(email, false);
        } catch (Exception e) {
            System.err.println("토큰 검증 실패: " + e.getMessage());
            return false; // 토큰이 유효하지 않은 경우 인증 실패로 간주
        }
    }

}
