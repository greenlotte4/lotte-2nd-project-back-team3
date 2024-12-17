package BackAnt.repository.chatting;

import BackAnt.entity.chatting.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    @Query("SELECT c FROM Channel c JOIN ChannelMember cm " +
            "ON cm.channel = c " +
            "WHERE (cm.user.id = :memberId AND c.channelPrivacy = true) " +
            "OR c.channelPrivacy=false ")
    List<Channel> findVisibleChannelByMemberId(@Param("memberId") Long memberId);
}
