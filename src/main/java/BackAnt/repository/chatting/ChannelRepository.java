package BackAnt.repository.chatting;

import BackAnt.entity.chatting.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
}
