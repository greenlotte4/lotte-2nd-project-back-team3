package BackAnt.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForbiddenWordDTO {
    private Long id;
    private String word;
}