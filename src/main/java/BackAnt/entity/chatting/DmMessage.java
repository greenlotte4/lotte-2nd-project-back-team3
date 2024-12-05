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
public class DmMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Dm dm;

    @ManyToOne
    private User sender;

    private String content;

    private Boolean isRead = false; // 읽음 여부

    // 추가된 생성자
    public DmMessage(Dm dm, User sender, String content) {
        this.dm = dm;
        this.sender = sender;
        this.content = content;
        this.isRead = false; // 기본값 false
    }
    // isRead 상태 변경을 위한 메서드
    public void markAsRead() {
        this.isRead = true;
    }
    // isRead 상태 조회를 위한 getter
    public Boolean getIsRead() {
        return isRead;
    }
}
