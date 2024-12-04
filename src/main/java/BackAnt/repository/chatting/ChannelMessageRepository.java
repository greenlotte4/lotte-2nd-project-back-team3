package BackAnt.repository.chatting;

import BackAnt.entity.chatting.ChannelMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelMessageRepository extends JpaRepository<ChannelMessage, Long> {
    // 채널 ID와 메시지 내용에 keyword가 포함된 메시지를 찾는 쿼리
    @Query("SELECT cm FROM ChannelMessage cm WHERE cm.channel.id = :channelId AND cm.content LIKE %:keyword%")
    List<ChannelMessage> findMessagesByKeywordAndChannel(@Param("channelId") Long channelId, @Param("keyword") String keyword);
}
