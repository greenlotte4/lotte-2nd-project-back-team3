package BackAnt.dto.RequestDTO;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRequestDTO {
    private String uid;
    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private Long companyId; // 회사 ID 매핑
}
