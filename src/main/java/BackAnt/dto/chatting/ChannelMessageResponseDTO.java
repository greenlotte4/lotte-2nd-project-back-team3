package BackAnt.dto.chatting;

import BackAnt.entity.chatting.ChannelMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelMessageResponseDTO {
    private Long id;
    private String content;
    private Long senderId;
    private String userName;
    private String userProfile;
    private Long channelId;
    private String channelName;
    private String createdAt;  // 메시지 시간 (로컬 시간 형식으로 반환)
    private String fileUrl; // 파일 URL 추가
    private String fileType; // 파일 MIME 타입 추가
    private Boolean canDelete; // 삭제 가능 여부


    // ChannelMessage 엔티티를 ChannelMessageResponseDTO로 변환하는 정적 메서드
    public static ChannelMessageResponseDTO fromEntity(ChannelMessage message) {
        return ChannelMessageResponseDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .userName(message.getSender().getName())
                .userProfile(message.getSender().getProfileImageUrl())
                .channelId(message.getChannel().getId())
                .channelName(message.getChannel().getName())
                .createdAt(message.getCreatedAt().toString())  // 메시지 시간
                .fileUrl(message.getFileUrl())  // 파일 URL 포함
                .fileType(message.getFileType())  // 파일 MIME 타입 반환
                .build();
    }

}