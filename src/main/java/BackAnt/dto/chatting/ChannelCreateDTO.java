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
public class ChannelCreateDTO {
    private String name;
    private Long userId;
    private boolean channelPrivacy; // 채널 공개 여부 추가
}
