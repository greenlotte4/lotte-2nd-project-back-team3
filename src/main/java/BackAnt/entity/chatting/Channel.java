package BackAnt.entity.chatting;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Channel extends Room {
    private String name;// 채널 이름
    private String description;// 채널 설명
    private boolean isPrivate;// 공개/비공개 여부
}
