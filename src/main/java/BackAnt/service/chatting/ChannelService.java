package BackAnt.service.chatting;

import BackAnt.dto.chatting.ChannelCreateDTO;
import BackAnt.entity.User;
import BackAnt.entity.chatting.Channel;
import BackAnt.repository.UserRepository;
import BackAnt.repository.chatting.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public Long createChannel(ChannelCreateDTO channelCreateDTO) {
        User user = userRepository.findById((long) channelCreateDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        Channel channel = Channel.create(channelCreateDTO.getName(), user);
        channelRepository.save(channel);
        return channel.getId();
    }
}
