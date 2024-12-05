package BackAnt.entity.chatting;

import BackAnt.entity.User;
import BackAnt.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Channel extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 채널의 고유 ID

    private String name; // 채널 이름

    @Column(name = "channel_privacy")
    private boolean channelPrivacy;  // 변경 후

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // 소유자 변경
    public void setOwner(User newOwner) {
        this.owner = newOwner;
    }

    public static Channel create(String name, User owner, boolean isPublic)
    {
        Channel channel = Channel.builder()
                .name(name)
                .owner(owner)
                .channelPrivacy(!isPublic)  // isPublic을 기반으로 isPrivate 값 설정
                .build();

        return channel;
    }

}
