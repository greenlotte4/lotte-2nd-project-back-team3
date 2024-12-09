package BackAnt.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DmCreateDTO {
    private Long creatorId; // 생성자
    private List<Long> receiverIds; // 상대방 사용자 ID
}