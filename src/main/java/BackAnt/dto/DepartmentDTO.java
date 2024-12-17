package BackAnt.dto;

import BackAnt.dto.user.UserDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DepartmentDTO {

    private Long id; // 부서 고유 ID
    private String name; // 부서명
    private Long company_id; // 소속 회사
    private List<UserDTO> users = new ArrayList<>(); // 부서에 속한 사용자들

}
