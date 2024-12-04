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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public List<ChannelMessageResponseDTO> getsearchChannelMessages(Long channelId, String keyword) {
        // 채널 ID와 키워드를 기준으로 메시지를 검색
        List<ChannelMessage> messages = channelMessageRepository.findMessagesByKeywordAndChannel(channelId, keyword);

        // null 체크 후 빈 리스트로 처리
        if (messages == null) {
            messages = new ArrayList<>();
        }

        // 검색된 메시지들을 DTO로 변환하여 반환
        return messages.stream()
                .map(ChannelMessageResponseDTO::fromEntity) // DTO 변환
                .collect(Collectors.toList());
    }




}
