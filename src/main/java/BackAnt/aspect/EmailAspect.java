package BackAnt.aspect;

import BackAnt.dto.landing.QnaResponseDTO;
import BackAnt.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Log4j2
public class EmailAspect {
    private final EmailService emailService;

    @Async// 이게 있으면 별도 쓰레드에서 비동기로 실행
    @AfterReturning(value = "execution(* BackAnt.service.landing.QnaService.updateAnswer(..))", returning = "response")
    public void sendEmailAfterUpdate(JoinPoint joinPoint, QnaResponseDTO response) {
        Object[] args = joinPoint.getArgs();
        Long id = (Long) args[0];
        String answer = (String) args[1];

        // Qna 정보 활용
        log.info("AOP 실행: id={}, answer={}", id, answer);

        // 이메일 전송 로직
        String targetEmail = response.getEmail(); // QnaResponseDTO에 이메일 필드가 있다고 가정
        String title = "문의내역에 답변이 작성되었습니다.";
        String body =
                "작성하신 문의내역에 답변이 작성되었습니다.\n" +
                        "확인하시겠습니까 ? \n" +
                        "페이지 링크 : http://localhost:5173/support";

        emailService.sendEmailMessage(targetEmail, title, body);
        log.info("이메일 전송 완료: {}", targetEmail);
    }

}
