package BackAnt.document.page;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/*
    날 짜 : 2024/11/29(금)
    담당자 : 황수빈
    내용 : Page Image 를 위한 mongoDB Document 생성
*/

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "PageImages")
public class PageImageDocument {
    @Id
    private String _id;

    private String name;
    private String path;

}
