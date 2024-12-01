package BackAnt.dto.chatting;

import BackAnt.entity.chatting.Channel;
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
    private int userId;
}
