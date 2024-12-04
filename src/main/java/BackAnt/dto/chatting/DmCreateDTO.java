package BackAnt.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DmCreateDTO {
    private Long receiverId; // 상대방 사용자 ID
    private String firstMessage; // 첫 메시지 내용
}