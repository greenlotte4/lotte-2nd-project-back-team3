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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private List<ChannelMember> members;

    // 채널 생성 메서드 수정
    public Long createChannel(ChannelCreateDTO channelCreateDTO) {
        User user = userRepository.findById(channelCreateDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        Channel channel = Channel.create(channelCreateDTO.getName(), user, channelCreateDTO.isPublic());
        channelRepository.save(channel);

        // 소유자를 채널 멤버로 추가
        ChannelMember channelMember = new ChannelMember(channel, user);
        channelMemberRepository.save(channelMember);

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
        Channel channel = channelRepository.findById(id).orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));
//        return new ChannelResponseDTO(channel);
        return ChannelResponseDTO.fromEntity(channel);
    }

    public void addChannelMember(Long channelId, ChannelMemberAddDTO channelMemberAddDTO) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));
        List<User> users = userRepository.findAllById(channelMemberAddDTO.getMemberIds());

        for (User user : users) {
            if(channelMemberRepository.findByChannelIdAndUserId(channel.getId(), user.getId()).isPresent()) {
                continue;
            }

            ChannelMember channelMember = new ChannelMember(channel, user);
            channelMemberRepository.save(channelMember);
        }
    }

    public void removeChannelMember(Long channelId, ChannelMemberAddDTO channelMemberAddDTO) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));
        List<User> users = userRepository.findAllById(channelMemberAddDTO.getMemberIds());

        for (User user : users) {
            ChannelMember channelMember = channelMemberRepository
                    .findByChannelIdAndUserId(channel.getId(), user.getId())
                    .orElseThrow(() -> new RuntimeException("사용자는 이 채널의 멤버가 아닙니다"));
            channelMemberRepository.delete(channelMember);

        }
    }

    /*// 소유자 자동 변경 후 나가기
    public void transferOwnershipAndLeave(Long channelId, Long userId) {
        // 채널 정보 조회
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        // 현재 소유자 조회
        User currentOwner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("현재 소유자를 찾을 수 없습니다"));

        // 현재 소유자만 소유권을 변경할 수 있도록 처리
        if (!channel.getOwner().equals(currentOwner)) {
            throw new RuntimeException("현재 소유자만 소유권을 변경할 수 있습니다");
        }

        // 채널에 포함된 멤버들 조회 (소유자 제외)
        List<ChannelMember> members = channelMemberRepository.findByChannel(channel);
        if (members.size() <= 1) {
            throw new RuntimeException("채널에 다른 멤버가 없어 소유자 변경을 할 수 없습니다");
        }
        // 채널에 멤버가 1명만 남은 경우, 소유자가 나가면 채널 삭제
        if (members.size() == 1) {
            // 소유자가 유일한 멤버일 경우, 채널 삭제
            channelRepository.delete(channel);
            return;  // 채널이 삭제되었으므로 더 이상 진행할 필요 없음
        }

        // 첫 번째로 입장한 멤버를 새로운 소유자로 설정 (현재 소유자 제외)
        User newOwner = members.stream()
                .filter(member -> !member.getUser().equals(currentOwner))
                .map(ChannelMember::getUser)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("새로운 소유자를 찾을 수 없습니다"));

        // 소유자 변경
        channel.setOwner(newOwner);
        channelRepository.save(channel);

        // 기존 소유자 채널 멤버에서 제거
        ChannelMember channelMember = channelMemberRepository
                .findByChannelIdAndUserId(channel.getId(), currentOwner.getId())
                .orElseThrow(() -> new RuntimeException("소유자는 채널 멤버가 아닙니다"));
        channelMemberRepository.delete(channelMember);
    }*/
    public void transferOwnershipAndLeave(Long channelId, Long userId) {
        // 채널 정보 조회
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        // 현재 소유자 조회
        User currentOwner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("현재 소유자를 찾을 수 없습니다"));

        // 현재 소유자만 소유권을 변경할 수 있도록 처리
        if (!channel.getOwner().equals(currentOwner)) {
            throw new RuntimeException("현재 소유자만 소유권을 변경할 수 있습니다");
        }

        // 채널에 포함된 멤버들 조회 (소유자 제외)
        List<ChannelMember> members = channelMemberRepository.findByChannel(channel);

        // 채널에 멤버가 1명만 남은 경우, 소유자가 나가면 채널 삭제
        if (members.size() == 1) {
            // 소유자가 유일한 멤버일 경우, 채널 삭제
            channelRepository.delete(channel);
            return;  // 채널이 삭제되었으므로 더 이상 진행할 필요 없음
        }

        // 새로운 소유자 찾기 (현재 소유자 제외)
        Optional<User> newOwner = members.stream()
                .filter(member -> !member.getUser().equals(currentOwner))
                .map(ChannelMember::getUser)
                .findFirst();

        // 새로운 소유자가 없으면 채널 삭제
        if (newOwner.isEmpty()) {
            channelRepository.delete(channel);
            return;  // 채널 삭제 후 더 이상 진행할 필요 없음
        }

        // 소유자 변경
        channel.setOwner(newOwner.get());
        channelRepository.save(channel);

        // 기존 소유자 채널 멤버에서 제거
        ChannelMember channelMember = channelMemberRepository
                .findByChannelIdAndUserId(channel.getId(), currentOwner.getId())
                .orElseThrow(() -> new RuntimeException("소유자는 채널 멤버가 아닙니다"));
        channelMemberRepository.delete(channelMember);
    }




}

