package BackAnt.document.page;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/*
    날 짜 : 2024/11/29(금)
    담당자 : 황수빈
    내용 : Page 저장을 위한 mongoDB Document 생성
*/

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "Page")
public class PageDocument {
    @Id
    private String _id; // int X - mongoDB Id는 String 으로 해야 랜덤값으로 들어감

    private String title;
    private String content; // JSON 형식의 데이터

    private String uid; // 작성자


}
