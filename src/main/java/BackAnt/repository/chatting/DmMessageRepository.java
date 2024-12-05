package BackAnt.repository.chatting;

import BackAnt.entity.chatting.Dm;
import BackAnt.entity.chatting.DmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DmMessageRepository extends JpaRepository<DmMessage, Long> {
    List<DmMessage> findAllByDm(Dm dm);
}
