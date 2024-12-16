package BackAnt.dto.page;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDTO {

    private String _id;
    private String title;
    private String content;
    private String owner;
    private String ownerName;
    private String ownerImage;

    private String uid;
    private Boolean isTemplate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int isDeleted;

    private String componentId;
    //  private Map<String, Object> content; - JSON 저장 방식

}
