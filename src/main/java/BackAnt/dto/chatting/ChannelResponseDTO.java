package BackAnt.dto.chatting;

import BackAnt.entity.chatting.Channel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelResponseDTO {
    private Long id;
    private String name;
    @JsonProperty("isPrivate")
    private boolean isPrivate;
    private Long ownerId;





    // 생성자
    public ChannelResponseDTO(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.isPrivate = channel.isPrivate();
        this.ownerId = channel.getOwner() != null ? channel.getOwner().getId() : 0;
    }


    // 정적 함수
    public static ChannelResponseDTO fromEntity(Channel channel) {
        return ChannelResponseDTO.builder()
                .id(channel.getId())
                .name(channel.getName())
                .isPrivate(channel.isPrivate())
                .ownerId(channel.getOwner() != null ? channel.getOwner().getId() : 0)
                .build();
    }
}
