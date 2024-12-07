package BackAnt.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelMessageCreateDTO {
    private Long senderId;  // 사용자 ID
    private String content; // 메시지 내용
}
