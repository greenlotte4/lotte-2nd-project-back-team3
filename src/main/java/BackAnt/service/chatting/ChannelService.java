package BackAnt.service.chatting;

import BackAnt.dto.chatting.ChannelCreateDTO;
import BackAnt.dto.chatting.ChannelMemberAddDTO;
import BackAnt.dto.chatting.ChannelMemberResponseDTO;
import BackAnt.dto.chatting.ChannelResponseDTO;
import BackAnt.entity.User;
import BackAnt.entity.chatting.Channel;
import BackAnt.entity.chatting.ChannelMember;
import BackAnt.repository.UserRepository;
import BackAnt.repository.chatting.ChannelMemberRepository;
import BackAnt.repository.chatting.ChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private List<ChannelMember> members;

    // 채널 생성 메서드 수정
    public Long createChannel(ChannelCreateDTO channelCreateDTO) {

        System.out.println("channelCreateDTO = " + channelCreateDTO);
        User user = userRepository.findById(channelCreateDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        Channel channel = Channel.create(channelCreateDTO.getName(), user, channelCreateDTO.isChannelPrivacy());
        channelRepository.save(channel);
        log.info("여기는 서비스 (채널 생성) : "+channel);

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
        return ChannelResponseDTO.fromEntity(channel);
    }

    public List<ChannelMemberResponseDTO> addChannelMember(Long channelId, ChannelMemberAddDTO channelMemberAddDTO) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));
        List<User> users = userRepository.findAllById(channelMemberAddDTO.getMemberIds());
        List<ChannelMemberResponseDTO> addMembers = new ArrayList<>();
        for (User user : users) {
            if(channelMemberRepository.findByChannelIdAndUserId(channel.getId(), user.getId()).isPresent()) {
                continue;
            }

            ChannelMember channelMember = new ChannelMember(channel, user);
            addMembers.add(ChannelMemberResponseDTO.fromEntity(channelMember));
            channelMemberRepository.save(channelMember);
        }

        return addMembers;
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

    public void transferOwnershipAndLeave(Long channelId, Long userId) {
        // 채널 정보 조회
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        // 현재 소유자 조회
        User currentOwner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("현재 소유자를 찾을 수 없습니다"));



        // 채널에 포함된 멤버들 조회 (소유자 제외)
        List<ChannelMember> members = channelMemberRepository.findByChannel(channel);

        // 채널에 멤버가 1명만 남은 경우,소유자가 나가면 채널 삭제
        if (members.size() == 1) {
            // 소유자가 유일한 멤버일 경우, 채널 삭제
            channelMemberRepository.delete(members.get(0));
            channelRepository.delete(channel);
            return;  // 채널이 삭제되었으므로 더 이상 진행할 필요 없음
        }
        // 현재 소유자만 소유권을 변경할 수 있도록 처리
        if (channel.getOwner().equals(currentOwner)) {
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
        }
        // 기존 소유자 채널 멤버에서 제거
        ChannelMember channelMember = channelMemberRepository
                .findByChannelIdAndUserId(channel.getId(), currentOwner.getId())
                .orElseThrow(() -> new RuntimeException("소유자는 채널 멤버가 아닙니다"));
        channelMemberRepository.delete(channelMember);
    }

    public List<ChannelMemberResponseDTO> getChannelMembers(Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        return channelMemberRepository.findByChannel(channel).stream()
                .map(ChannelMemberResponseDTO::fromEntity)
                .toList();
    }

    public void changeChannelTitle(Long channelId, String channelName) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        channel.changeName(channelName);
    }


    public List<ChannelResponseDTO> getVisibleChannel(Long memberId) {
        List<Channel> visibleChannels = channelRepository.findVisibleChannelByMemberId(memberId);
        return visibleChannels.stream().map(ChannelResponseDTO::fromEntity).toList();
    }

    public List<ChannelResponseDTO> getVisibleChannelByName(Long memberId, String channelName) {
        List<Channel> visibleChannels = channelRepository.findVisibleChannelByMemberIdAndChannelName(memberId, channelName);
        return visibleChannels.stream().map(ChannelResponseDTO::fromEntity).toList();
    }


    public long getUnreadCountByChannelIdAndMemberId(Long channelId, Long memberId) {
        ChannelMember channelMember = channelMemberRepository.findByChannelIdAndMemberId(channelId, memberId);
        if(channelMember == null)
        {
            return 0;
        }
        long unreadCount = channelMemberRepository.countUnreadCountByChannelIdAndReadDateTime(channelId, channelMember.getLastReadAt());
        return unreadCount;
    }
}

