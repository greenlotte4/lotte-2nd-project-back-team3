package BackAnt.dto;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponseDTO {
    private int id;
    private String title;
    private String content;
}
