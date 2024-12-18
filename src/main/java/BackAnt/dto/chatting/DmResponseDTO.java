package BackAnt.dto.chatting;

import BackAnt.entity.chatting.Dm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DmResponseDTO {
    private Long dmId;  // 생성된 디엠방 ID
    private String dmName;
    private String lastMessage;

    // 정적 팩토리 메서드 추가
    public static DmResponseDTO fromEntity(Dm dm, String dmName, String lastMessage) {
        return new DmResponseDTO(
                dm.getId(),
                dmName,
                lastMessage
        );
    }
}
