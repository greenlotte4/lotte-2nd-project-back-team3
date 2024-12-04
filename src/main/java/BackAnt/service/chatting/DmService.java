package BackAnt.service.chatting;

/*import BackAnt.dto.chatting.DmCreateDTO;
import BackAnt.dto.chatting.DmResponseDTO;*/
import BackAnt.entity.chatting.Dm;
import BackAnt.entity.chatting.DmMember;
import BackAnt.entity.chatting.DmMessage;
import BackAnt.entity.User;
import BackAnt.repository.chatting.DmRepository;
import BackAnt.repository.chatting.DmMemberRepository;
import BackAnt.repository.chatting.DmMessageRepository;
import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DmService {

    private final DmRepository dmRepository;
    private final DmMessageRepository dmMessageRepository;
    private final UserRepository userRepository;
    private final DmMemberRepository dmMemberRepository;

    // 보내는 사람, 받는 사람 조회 메서드 (중복 제거)
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }

    /*// 디엠방 생성 (1:1 비공개 채팅)
    @Transactional
    public DmResponseDTO createDm(DmCreateDTO dmCreateDTO, Long senderId) {
        User sender = getUserById(senderId);  // 보내는 사람
        User receiver = getUserById(dmCreateDTO.getReceiverId());  // 받는 사람

        // 기존 DM 방이 있으면 반환, 없으면 새로 생성
        Dm dm = findOrCreateDm(sender, receiver);

        // 첫 번째 메시지 생성은 외부에서 처리 (DM 방은 여기서만 관리)
        return new DmResponseDTO(dm.getId(), dmCreateDTO.getFirstMessage());
    }*/

    // 메시지 보내기 (기존 DM 방에서 메시지 송신)
    @Transactional
    public void sendMessage(Long dmId, Long senderId, String content) {
        User sender = getUserById(senderId);
        Dm dm = dmRepository.findById(dmId)
                .orElseThrow(() -> new RuntimeException("디엠 방을 찾을 수 없습니다"));

        DmMessage dmMessage = new DmMessage(dm, sender, content);
        dmMessageRepository.save(dmMessage);
    }

    // DM 방 중복 생성 방지 및 생성
    private Dm findOrCreateDm(User sender, User receiver) {
        return dmRepository.findByMembersContaining(sender).stream()
                .filter(dm -> dm.getMembers().contains(receiver))
                .findFirst()
                .orElseGet(() -> createNewDm(sender, receiver)); // 없으면 새로 생성
    }

    // 새 DM 방 생성
    private Dm createNewDm(User sender, User receiver) {
        Dm dm = Dm.builder().build();
        dmRepository.save(dm);

        // DM 멤버 추가
        dmMemberRepository.save(new DmMember(dm, sender));
        dmMemberRepository.save(new DmMember(dm, receiver));

        return dm;
    }


}
