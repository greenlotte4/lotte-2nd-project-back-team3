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

    // 특정 메시지에 대해 읽지 않은 사용자 수를 계산하는 쿼리
    @Query("SELECT COUNT(m) FROM ChannelMessage m WHERE m.channel = :channel AND m.isRead = false AND m.id = :messageId")
    Long countByIsReadFalseAndChannelAndMessage(@Param("channel") Channel channel, @Param("messageId") Long messageId);
}
