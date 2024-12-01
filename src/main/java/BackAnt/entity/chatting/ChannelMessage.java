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
public class ChannelMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 메시지의 고유 ID

    @Column(length = 500)
    private String content; // 메시지 내용 (최대 500자)

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender; // 메시지를 전송한 사용자

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel; // 메시지가 전송된 채널
}
