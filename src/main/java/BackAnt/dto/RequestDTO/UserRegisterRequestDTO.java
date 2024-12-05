package BackAnt.dto.RequestDTO;

import BackAnt.entity.Department;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequestDTO {

    private String name;
    private String uid;
    private String password;
    private String nick;
    private String phoneNumber;
    private String profileImageUrl;
    private Long tokenid;
    private String email;
    private String role;
    private String position;

    private Department department;
}
