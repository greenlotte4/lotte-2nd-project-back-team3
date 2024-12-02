package BackAnt.repository.chatting;

import BackAnt.entity.chatting.ChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
    @Query("SELECT c FROM ChannelMember c WHERE c.channel.id = :channelId AND c.user.id = :userId")
    public Optional<ChannelMember> findByChannelIdAndUserId(@Param("channelId") long channelId,@Param("userId") long userId);
}
