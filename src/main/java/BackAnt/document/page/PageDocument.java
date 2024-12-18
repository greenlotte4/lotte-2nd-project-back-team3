package BackAnt.document.page;

import BackAnt.dto.page.PageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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
    private String owner; // 주인
    private String ownerName;
    private String ownerImage;

    private String uid; // 작성자
    @JsonProperty("isTemplate")
    private Boolean isTemplate;

    private LocalDateTime deletedAt = null;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static PageDTO convertToDTO(PageDocument page) {
        return PageDTO.builder()
                ._id(page.get_id())
                .title(page.getTitle())
                .owner(page.getOwner())
                .ownerName(page.getOwnerName())
                .ownerImage(page.getOwnerImage())
                .content(page.getContent())
                .updatedAt(page.getUpdatedAt())
                .build();
    }
}

