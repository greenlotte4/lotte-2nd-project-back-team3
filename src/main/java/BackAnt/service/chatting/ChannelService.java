package BackAnt.service.chatting;

import BackAnt.dto.chatting.ChannelCreateDTO;
import BackAnt.dto.chatting.ChannelMemberAddDTO;
import BackAnt.dto.chatting.ChannelResponseDTO;
import BackAnt.entity.User;
import BackAnt.entity.chatting.Channel;
import BackAnt.entity.chatting.ChannelMember;
import BackAnt.repository.UserRepository;
import BackAnt.repository.chatting.ChannelMemberRepository;
import BackAnt.repository.chatting.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ChannelMemberRepository channelMemberRepository;

    public Long createChannel(ChannelCreateDTO channelCreateDTO) {
        User user = userRepository.findById(channelCreateDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        Channel channel = Channel.create(channelCreateDTO.getName(), user);
        channelRepository.save(channel);
        return channel.getId();
    }

    public List<ChannelResponseDTO> getAllChannels() {
        // map은 기존값을 바탕으로 새로운 형태를 만드는 함수
        List<ChannelResponseDTO> result = channelRepository.findAll()
                .stream()
                .map(ChannelResponseDTO::new)
//               .map(ChannelResponseDTO::fromEntity)
                .toList();

        return result;
    }

    public ChannelResponseDTO getChannel(Long id) {
        Channel channel = channelRepository.findById(id).orElseThrow(() -> new RuntimeException("Channel not found"));
//        return new ChannelResponseDTO(channel);
        return ChannelResponseDTO.fromEntity(channel);
    }

    public void addChannelMember(Long channelId, ChannelMemberAddDTO channelMemberAddDTO) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("Channel not found"));
        List<User> users = userRepository.findAllById(channelMemberAddDTO.getMemberIds());

        for (User user : users) {
            if(channelMemberRepository.findByChannelIdAndUserId(channel.getId(), user.getId()).isPresent()) {
                continue;
            }

            ChannelMember channelMember = new ChannelMember(channel, user);
            channelMemberRepository.save(channelMember);
        }
    }
}
