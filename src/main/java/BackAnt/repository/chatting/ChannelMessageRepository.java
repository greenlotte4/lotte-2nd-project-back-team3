package BackAnt.repository.chatting;

import BackAnt.entity.chatting.Channel;
import BackAnt.entity.chatting.ChannelMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelMessageRepository extends JpaRepository<ChannelMessage, Long> {

    // 키워드로 메시지를 검색하는 쿼리
    @Query("SELECT cm FROM ChannelMessage cm WHERE cm.channel.id = :channelId AND cm.content LIKE %:keyword%")
    List<ChannelMessage> findMessagesByKeywordAndChannel(@Param("channelId") Long channelId, @Param("keyword") String keyword);

    // 채널의 모든 메시지 조회
    List<ChannelMessage> findAllByChannel(Channel channel);
}
