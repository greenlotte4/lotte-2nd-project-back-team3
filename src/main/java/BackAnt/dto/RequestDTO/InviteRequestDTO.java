package BackAnt.dto.RequestDTO;

import BackAnt.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    날짜 : 2024/12/04
    이름 : 최준혁
    내용 : Invite 요청을 위한 DTO
*/

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteRequestDTO {

    private String email; // 초대받은 사용자의 이메일
    private String name; // 초대받은 사용자의 이름
    private String position; // 직위
    private String phoneNumber; // 연락처
    private Long department; // 부서 ID
    private String role; // 초대받은 사용자의 역할
    private String note; // 초대 메모
}
