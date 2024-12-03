package BackAnt.dto;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
    //  private Map<String, Object> content; - JSON 저장 방식

}
