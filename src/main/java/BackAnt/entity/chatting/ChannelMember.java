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
public class ChannelMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 채널 멤버의 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel; // 멤버가 소속된 채널

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 채널 멤버로 참여 중인 사용자

    public ChannelMember(Channel channel, User user) {
        this.channel = channel;
        this.user = user;
    }
}
