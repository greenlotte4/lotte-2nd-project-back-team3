package BackAnt.repository.chatting;

import BackAnt.entity.chatting.ChannelMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelMessageRepository extends JpaRepository<ChannelMessage, Long> {
}
