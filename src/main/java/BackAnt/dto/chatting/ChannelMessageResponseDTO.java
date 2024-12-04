package BackAnt.dto.chatting;

import BackAnt.entity.chatting.ChannelMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelMessageResponseDTO {
    private Long id;
    private String content;
    private Long userId;
    private String userName;
    private Long channelId;
    private String channelName;

    // ChannelMessage 엔티티를 ChannelMessageResponseDTO로 변환하는 정적 메서드
    public static ChannelMessageResponseDTO fromEntity(ChannelMessage message) {
        return ChannelMessageResponseDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .userId(message.getSender().getId())
                .userName(message.getSender().getName())
                .channelId(message.getChannel().getId())
                .channelName(message.getChannel().getName())
                .build();
    }
}