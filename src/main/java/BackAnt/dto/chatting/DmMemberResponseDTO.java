package BackAnt.dto.chatting;

import BackAnt.entity.chatting.DmMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmMemberResponseDTO {
    private Long userId;
    private String userName;
    private String profileImageUrl;

    public static DmMemberResponseDTO fromEntity(DmMember dmMember) {
        return DmMemberResponseDTO.builder()
                .userId(dmMember.getUser().getId())
                .userName(dmMember.getUser().getName())
                .profileImageUrl(dmMember.getUser().getProfileImageUrl())
                .build();
    }
}
