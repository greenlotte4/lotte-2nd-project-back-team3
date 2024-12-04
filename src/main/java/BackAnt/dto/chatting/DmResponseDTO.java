package BackAnt.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DmResponseDTO {
    private Long dmId;  // 생성된 디엠방 ID
    private String message;  // 첫 메시지 내용
}
