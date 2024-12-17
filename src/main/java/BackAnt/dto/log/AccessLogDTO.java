package BackAnt.dto.log;

import BackAnt.entity.AccessLog;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
public class AccessLogDTO {

    private Long id;
    private String userId;
    private String ipAddress;
    private String urlPath;
    private String httpMethod;
    private String accessTime; // ISO 형식의 문자열로 변환된 시간
    private String methodDescription;

    public AccessLogDTO(AccessLog accessLog) {
        this.id = Long.valueOf(accessLog.getId());
        this.userId = accessLog.getUserId();
        this.ipAddress = accessLog.getIpAddress();
        this.urlPath = accessLog.getUrlPath();
        this.httpMethod = accessLog.getHttpMethod();
        this.accessTime = accessLog.getAccessTime()
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME); // ISO 8601 형식
        this.methodDescription = accessLog.getMethodDescription();
    }
}
