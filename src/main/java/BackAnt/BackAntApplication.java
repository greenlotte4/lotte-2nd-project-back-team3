package BackAnt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
/*
    작업 내역
    - 2024/12/03(화) 황수빈 - 어노테이션 추가


 */
@EnableMongoAuditing // mongoDB 수정된 시간 기록을 위해 추가
@EnableJpaAuditing
@SpringBootApplication
public class BackAntApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackAntApplication.class, args);
    }

}
