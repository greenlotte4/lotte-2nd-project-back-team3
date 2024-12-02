package BackAnt.dto;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequestDTO {
    private String _id;
    private String title;
    private String content;
    private String uid;










    //  private Map<String, Object> content; - JSON 저장 방식

}
