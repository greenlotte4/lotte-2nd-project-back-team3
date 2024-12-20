package BackAnt.dto.landing;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class QnaSearchDTO {


    private String email; // 이메일
    private String tempPassword; // 임시 비밀번호

}
