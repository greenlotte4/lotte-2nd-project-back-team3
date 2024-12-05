package BackAnt.dto.chatting;

import BackAnt.entity.chatting.DmMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmMessageResponseDTO {
    private Long id;
    private String content;
    private Long senderId;
    private String senderName;
    private Long dmId;  // DM ID
    private Boolean isRead;
    private String timestamp;  // 메시지 시간 (로컬 시간 형식으로 반환)

    // DmMessage 엔티티를 DmMessageResponseDTO로 변환하는 정적 메서드
    public static DmMessageResponseDTO fromEntity(DmMessage dmMessage) {
        return DmMessageResponseDTO.builder()
                .id(dmMessage.getId())
                .content(dmMessage.getContent())
                .senderId(dmMessage.getSender().getId())
                .senderName(dmMessage.getSender().getName())
                .dmId(dmMessage.getDm().getId())
                .isRead(dmMessage.getIsRead())
                .timestamp(dmMessage.getCreatedAt().toString())  // 메시지 시간
                .build();
    }
}