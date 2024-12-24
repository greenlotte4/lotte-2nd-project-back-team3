package BackAnt.dto.page;


import BackAnt.entity.User;
import BackAnt.entity.page.PageCollaborator;
import BackAnt.repository.UserRepository;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageCollaboratorDTO {

    private int id;

    private String pageId;

    private Long user_id;
    private String name;
    private String uid;
    private String uidImage;

    private LocalDateTime invitedAt; // 프로젝트 초대 날짜
    private Boolean isOwner; //  최고관리자인지 아닌지 (프로젝트 생성할때 만든사람이 최고관리자가 됨)
    private int type; // 프로젝트 권한 (ADMIN, WRITE, READ)

    // DTO에서 엔티티로 변환하는 메서드
    public PageCollaborator toEntity(UserRepository userRepository) {
        // uid로 User를 찾아서 설정
        User user = userRepository.findById(this.user_id)
                .orElseThrow(() -> new RuntimeException("User not found with uid: " + this.user_id));

        return PageCollaborator.builder()
                .pageId(this.pageId)
                .user(user)
                .invitedAt(this.invitedAt)
                .isOwner(this.isOwner)
                .type(this.type)
                .build();
    }


}
