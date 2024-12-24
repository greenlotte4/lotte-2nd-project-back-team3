package BackAnt.entity.page;

import BackAnt.dto.page.PageCollaboratorDTO;
import BackAnt.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PageCollaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 무결성을 위한 AI PK
    private int id;

    private String pageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user; // 소속된 사용자

    @CreationTimestamp
    private LocalDateTime invitedAt; // 프로젝트 초대 날짜
    private Boolean isOwner; //  최고관리자인지 아닌지 (프로젝트 생성할때 만든사람이 최고관리자가 됨)
    private int type; // 프로젝트 권한 ( 협업자 초대 - 0 읽기/쓰기 = 1, 읽기=2 )



    public PageCollaboratorDTO toDTO() {
        return PageCollaboratorDTO.builder()
                .id(this.id)
                .pageId(this.pageId)
                .user_id(this.user.getId())
                .uid(this.user.getUid())// User의 uid를 가져옴
                .name(this.user.getName())
                .uidImage(this.user.getProfileImageUrl())  // User의 이미지 필드가 있을 경우 가져옴
                .invitedAt(this.invitedAt)
                .isOwner(this.isOwner)
                .type(this.type)
                .build();
    }
}
