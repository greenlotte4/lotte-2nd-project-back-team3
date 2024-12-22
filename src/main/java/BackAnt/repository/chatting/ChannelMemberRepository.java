package BackAnt.repository.chatting;

import BackAnt.entity.chatting.Channel;
import BackAnt.entity.chatting.ChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
    @Query("SELECT c FROM ChannelMember c WHERE c.channel.id = :channelId AND c.user.id = :userId")
    public Optional<ChannelMember> findByChannelIdAndUserId(@Param("channelId") long channelId, @Param("userId") long userId);

    // 특정 채널 내 전체 멤버 수 조회
    @Query("SELECT COUNT(cm) FROM ChannelMember cm WHERE cm.channel.id = :channelId")
    long countMembersInChannel(Long channelId);

    // channelId로 지정된 채널 내에서, 특정 메시지가 생성된 시간(messageCreatedAt) 이후(또는 해당 시각 포함)에
// 메시지를 읽은(lastReadAt >= messageCreatedAt) 멤버 수를 카운트한다.
// 즉, 이 쿼리는 해당 채널의 전체 멤버 중 주어진 메시지 기준으로 이미 그 메시지 혹은 그 이후의 메시지를 읽은
// 멤버가 몇 명인지를 알려준다.
    @Query("SELECT COUNT(cm) FROM ChannelMember cm WHERE cm.channel.id = :channelId AND cm.lastReadAt IS NOT NULL AND cm.lastReadAt >= :messageCreatedAt")
    long countMembersReadOrBeyond(Long channelId, LocalDateTime messageCreatedAt);


    // 특정 채널내 특정 멤버의 ChannelMember 엔티티 찾기
    @Query("SELECT cm FROM ChannelMember cm WHERE cm.channel.id = :channelId AND cm.user.id = :memberId")
    ChannelMember findByChannelIdAndMemberId(Long channelId, Long memberId);

    @Query("SELECT COUNT(cm) FROM ChannelMessage cm WHERE cm.channel.id = :channelId AND cm.createdAt  >= :readDateTime")
    long countUnreadCountByChannelIdAndReadDateTime(@Param("channelId") long channelId, @Param("readDateTime") LocalDateTime readDateTime);

    List<ChannelMember> findByChannel(Channel channel);
}
