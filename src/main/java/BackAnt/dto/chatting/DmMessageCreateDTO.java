package BackAnt.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DmMessageCreateDTO {
    private String message;  // 메시지 내용
}