package BackAnt.dto.RequestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 필드 무시
public class AdminRequestDTO {
    private String uid;
    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private Long companyId; // 회사 ID 매핑
}
