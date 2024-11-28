package BackAnt.entity.chatting;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
/*@AllArgsConstructor*/
@NoArgsConstructor
@SuperBuilder
public class Dm extends Room {
// DM은 추가 필드 없음
}
