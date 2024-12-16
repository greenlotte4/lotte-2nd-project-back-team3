package BackAnt.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "accesslog") // MongoDB 컬렉션 이름과 매핑
public class AccessLog {

    @Id
    private String id; // MongoDB의 기본 키 (_id 필드)

    private String userId;      // 사용자 ID
    private String ipAddress;   // IP 주소
    private String urlPath;     // 요청 URL 경로
    private String httpMethod;  // HTTP 메서드 (GET, POST 등)
    private LocalDateTime accessTime; // 요청 시간
    private String methodDescription; // 설명 필드 추가

    // 필요한 매개변수를 받는 생성자 추가
    public AccessLog(String userId, String ipAddress, String urlPath, String httpMethod, LocalDateTime accessTime, String methodDescription) {
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.urlPath = urlPath;
        this.httpMethod = httpMethod;
        this.accessTime = accessTime;
        this.methodDescription = methodDescription;
    }
}