package BackAnt.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelMessageSocketDTO {
    Long id;
    Long senderId;
    String userName;
    String content;
    String createdAt;
}
