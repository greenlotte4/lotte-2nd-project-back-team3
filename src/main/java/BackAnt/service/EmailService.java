package BackAnt.service;

import BackAnt.JWT.JwtProvider;
import BackAnt.entity.User;
import BackAnt.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    // 이메일 기반 인증 상태 관리
    private final ConcurrentHashMap<String, Boolean> emailVerificationStatus = new ConcurrentHashMap<>();
    private final SpringTemplateEngine templateEngine;

    // YML에서 읽어온 프론트엔드 URL
    @Value("${frontend.url}")
    private String frontendUrl;

    // 공통 이메일 전송 로직
    public void sendEmailMessage(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("Email sent to: " + to);
    }

    // 초대 이메일 전송
    public void sendInviteEmailWithTemplate(String email, String name, String department, String inviteToken) {
        try {
            // 초대 링크 생성
            String inviteLink = frontendUrl + "/register?token=" + inviteToken;

            // Thymeleaf 컨텍스트 생성
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("department", department);
            context.setVariable("inviteLink", inviteLink);

            // HTML 템플릿 처리
            String htmlContent = templateEngine.process("invite-email", context);

            // MIME 이메일 생성
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 이메일 설정
            helper.setTo(email);
            helper.setSubject("멤버 초대");
            helper.setText(htmlContent, true); // HTML 내용을 포함

            // 이메일 전송
            mailSender.send(mimeMessage);
            System.out.println("HTML 이메일 전송 완료: " + email);
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
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

    // 이메일로 아이디 찾기
    public int idCheck(String to, String subject, String body){

        Random random = new Random();
        int verificationCode = 100000 + random.nextInt(900000);

        // 이메일 본문
        String fullBody = body + "\n\n인증 번호:\n" + verificationCode;

        // 이메일 전송
        sendEmailMessage(to, subject, fullBody);

        return verificationCode;

    }
}
