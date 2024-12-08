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
    private String uid;
    private LocalDateTime updatedAt;

    private int isDeleted;

    private String componentId;
    //  private Map<String, Object> content; - JSON 저장 방식

}