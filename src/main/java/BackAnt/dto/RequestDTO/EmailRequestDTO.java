package BackAnt.dto.RequestDTO;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String body;
}
