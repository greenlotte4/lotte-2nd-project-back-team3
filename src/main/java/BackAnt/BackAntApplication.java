package BackAnt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
    작업 내역
    - 2024/12/03(화) 황수빈 - 어노테이션 추가
    - 2024/12/12(목) 최준혁 - EnableScheduling 어노테이션 추가
 */
@EnableMongoAuditing // mongoDB 수정된 시간 기록을 위해 추가
@EnableScheduling // 스케줄러 활성화 (준혁)
@EnableJpaAuditing
@SpringBootApplication
@EnableAsync // 황수빈 - 답변 등록 후 이메일 전송 비동기로 이용하기 위해 사용
public class BackAntApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackAntApplication.class, args);
    }

}
