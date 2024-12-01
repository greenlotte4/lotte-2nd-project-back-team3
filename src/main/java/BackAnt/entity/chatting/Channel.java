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

    private boolean isPrivate; // 채널의 공개/비공개 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    public static Channel create(String name, User owner)
    {
        Channel channel = Channel.builder()
                .name(name)
                .owner(owner)
                .build();

        return channel;
    }
}
