package BackAnt.repository.chatting;

import BackAnt.entity.chatting.DmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DmMessageRepository extends JpaRepository<DmMessage, Long> {
}
