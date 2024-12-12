package BackAnt.dto.chatting;

import BackAnt.entity.chatting.ChannelMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelMemberResponseDTO {
    private Long userId;
    private String userName;
    private String profileImageUrl;

    public static ChannelMemberResponseDTO fromEntity(ChannelMember channelMember) {
        return ChannelMemberResponseDTO.builder()
                .userId(channelMember.getUser().getId())
                .userName(channelMember.getUser().getName())
                .profileImageUrl(channelMember.getUser().getProfileImageUrl())
                .build();
    }
}
