package BackAnt.dto.RequestDTO;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    private String uid;
    private String password;
}
