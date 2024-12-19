package BackAnt.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelMessageSocketDTO {
    Long id;
    Long senderId;
    String userName;
    String content;
    String createdAt;
    Long channelId;
    private String fileUrl; // 파일 URL 추가
    private Boolean isImage; // 이미지 여부 추가
    private String fileType; // 파일 타입 (image/jpeg, application/pdf 등)
}
