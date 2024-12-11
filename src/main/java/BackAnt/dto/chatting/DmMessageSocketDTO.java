package BackAnt.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmMessageSocketDTO {
    Long id;
    Long senderId;
    String content;
    String createdAt;
}
