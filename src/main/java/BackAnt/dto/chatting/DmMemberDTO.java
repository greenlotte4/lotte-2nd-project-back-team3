package BackAnt.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DmMemberDTO {
    private Long userId;  // 유저 ID (보내는 사람 또는 받는 사람)
    private Long dmId;    // 디엠방 ID
}
