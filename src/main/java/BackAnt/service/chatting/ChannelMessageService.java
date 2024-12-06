package BackAnt.service.chatting;

import BackAnt.dto.chatting.ChannelMessageCreateDTO;
import BackAnt.dto.chatting.ChannelMessageResponseDTO;
import BackAnt.entity.User;
import BackAnt.entity.chatting.Channel;
import BackAnt.entity.chatting.ChannelMessage;
import BackAnt.repository.UserRepository;
import BackAnt.repository.chatting.ChannelMessageRepository;
import BackAnt.repository.chatting.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelMessageService {
    private final ChannelMessageRepository channelMessageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    // 채널 메시지 보내기
    public Long sendMessage(ChannelMessageCreateDTO dto) {
        // 사용자 및 채널 검증
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Channel channel = channelRepository.findById(dto.getChannelId())
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        // 메시지 생성
        ChannelMessage message = ChannelMessage.builder()
                .content(dto.getContent())
                .sender(sender)
                .channel(channel)
                .build();

        // 메시지 저장
        channelMessageRepository.save(message);

        return message.getId(); // 메시지 ID 반환
    }

    // 키워드로 채널 메시지 검색
    public List<ChannelMessageResponseDTO> getsearchChannelMessages(Long channelId, String keyword) {
        List<ChannelMessage> messages = channelMessageRepository.findMessagesByKeywordAndChannel(channelId, keyword);

        if (messages == null) {
            messages = new ArrayList<>();
        }

        return messages.stream()
                .map(message -> {
                    Long unreadCount = getUnreadCountForMessage(message);  // 읽지 않은 사람 수 계산
                    return ChannelMessageResponseDTO.fromEntity(message, unreadCount);  // unreadCount와 함께 DTO로 변환
                })
                .collect(Collectors.toList());
    }



    // 채널 메시지 조회
    @Transactional(readOnly = true)
    public List<ChannelMessageResponseDTO> getMessages(Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        List<ChannelMessage> messages = channelMessageRepository.findAllByChannel(channel);

        return messages.stream()
                .map(message -> {
                    Long unreadCount = getUnreadCountForMessage(message);  // 읽지 않은 사람 수 계산
                    return ChannelMessageResponseDTO.fromEntity(message, unreadCount);  // unreadCount와 함께 DTO로 변환
                })
                .collect(Collectors.toList());
    }

    // 채널 메시지 읽음 처리
    @Transactional
    public void markMessagesAsRead(Long channelId, Long userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        List<ChannelMessage> messages = channelMessageRepository.findAllByChannel(channel);

        // 메시지를 읽음 처리
        for (ChannelMessage message : messages) {
            // 본인이 보낸 메시지는 제외하고, 읽지 않은 메시지를 읽음 상태로 처리
            if (!message.getSender().getId().equals(userId) && !message.getIsRead()) {
                message.markAsRead();  // 읽음 상태로 설정
            }
        }
    }
    // 채널 메시지 읽지 않은 메시지 수 조회
    @Transactional(readOnly = true)
    public List<ChannelMessageResponseDTO> getMessagesWithUnreadCount(Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        List<ChannelMessage> messages = channelMessageRepository.findAllByChannel(channel);

        // 메시지 리스트에 읽지 않은 사람 수를 포함해서 DTO로 변환
        return messages.stream()
                .map(message -> {
                    Long unreadCount = getUnreadCountForMessage(message);  // 각 메시지에 대해 읽지 않은 사람 수 계산
                    return ChannelMessageResponseDTO.fromEntity(message, unreadCount);  // unreadCount와 함께 DTO로 변환
                })
                .collect(Collectors.toList());
    }

    private Long getUnreadCountForMessage(ChannelMessage message) {
        // 메시지 ID를 넘겨줘야 하므로 message.getId()를 사용합니다.
        return channelMessageRepository.countByIsReadFalseAndChannelAndMessage(message.getChannel(), message.getId());
    }


}
