package BackAnt.dto.page;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageCreateDTO {

    private String title;
    private String content;
    private String owner;
    private String ownerName;
    private String ownerImage;
    private Boolean isTemplate;

    private Integer companyRate;


}
