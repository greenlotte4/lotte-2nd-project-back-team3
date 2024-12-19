package BackAnt.service.chatting;

import BackAnt.dto.chatting.*;
import BackAnt.dto.common.ResultDTO;
import BackAnt.entity.chatting.Dm;
import BackAnt.entity.chatting.DmMember;
import BackAnt.entity.chatting.DmMessage;
import BackAnt.entity.User;
import BackAnt.repository.chatting.DmRepository;
import BackAnt.repository.chatting.DmMemberRepository;
import BackAnt.repository.chatting.DmMessageRepository;
import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

    private List<User> getUsersById(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    // 디엠방 생성 (1:1 비공개 채팅)
    @Transactional
    public ResultDTO<Long> createDm(DmCreateDTO dmCreateDTO) {
        User creator = getUserById(dmCreateDTO.getCreatorId());  // 생성하는 사람
        List<User> receivers = getUsersById(dmCreateDTO.getReceiverIds());  // 받는 사람

        if (receivers.isEmpty()) {
            throw new IllegalArgumentException("해당 유저가 없습니다.");
        }

        // 기존 DM 방이 있으면 반환, 없으면 새로 생성
        List<User> users = new ArrayList<>();
        users.add(creator);
        users.addAll(receivers);

        Dm dm = findOrCreateDm(users);

        // 첫 번째 메시지 생성은 외부에서 처리 (DM 방은 여기서만 관리)
        return new ResultDTO<Long>(dm.getId());
    }

    // 메시지 보내기 (기존 DM 방에서 메시지 송신)
    @Transactional
    public Long sendMessage(Long dmId, DmMessageCreateDTO dmMessageCreateDTO) {
        User sender = getUserById(dmMessageCreateDTO.getSenderId());
        Dm dm = dmRepository.findById(dmId)
                .orElseThrow(() -> new RuntimeException("디엠 방을 찾을 수 없습니다"));


        DmMessage dmMessage = new DmMessage(dm, sender, dmMessageCreateDTO.getContent());
        dmMessageRepository.save(dmMessage);
        return dmMessage.getId();
    }

    // DM 방 중복 생성 방지 및 생성
    private Dm findOrCreateDm(List<User> users) {

        Dm dm = findByMembersExactly(users).orElse(null);
        if (dm == null) {
            return createNewDm(users);
        }

        return dm;
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

    // 새 DM 방 생성
    private Dm createNewDm(List<User> users) {
        Dm dm = Dm.builder().build();
        dmRepository.save(dm);

        for (User user : users) {
            // DM 멤버 추가
            dmMemberRepository.save(new DmMember(dm, user));
        }

        return dm;
    }


    @Transactional(readOnly = true)
    public List<DmMessageResponseDTO> getMessages(Long dmId) {
        Dm dm = dmRepository.findById(dmId)
                .orElseThrow(() -> new RuntimeException("디엠 방을 찾을 수 없습니다"));

        List<DmMessage> messages = dmMessageRepository.findAllByDm(dm);

        // DmMessage -> DmMessageResponseDTO 변환
        return messages.stream()
                .map(dmMessage -> DmMessageResponseDTO.builder()
                        .id(dmMessage.getId())
                        .content(dmMessage.getContent())
                        .senderId(dmMessage.getSender().getId())
                        .userName(dmMessage.getSender().getName())
                        .userProfile(dmMessage.getSender().getProfileImageUrl())
                        .dmId(dmMessage.getDm().getId())
                        .isRead(dmMessage.getIsRead())
                        .createdAt(dmMessage.getCreatedAt().toString())
                        .build()
                )
                .collect(Collectors.toList());
    }


    @Transactional
    public void markMessagesAsRead(Long dmId, Long userId) {
        // DM 방 조회
        Dm dm = dmRepository.findById(dmId)
                .orElseThrow(() -> new RuntimeException("디엠 방을 찾을 수 없습니다"));

        // 해당 DM 방의 메시지 목록을 조회
        List<DmMessage> messages = dmMessageRepository.findAllByDm(dm);

        // 메시지를 읽음 처리
        for (DmMessage message : messages) {
            // 본인이 보낸 메시지는 제외하고, 읽지 않은 메시지를 읽음 상태로 처리
            if (!message.getSender().getId().equals(userId) && !message.getIsRead()) {
                message.markAsRead();  // 읽음 상태로 설정
            }
        }
    }


    public Optional<Dm> findByMembersExactly(List<User> users) {
        List<Dm> candidates = dmRepository.findAllByUsersIn(users);
        return candidates.stream()
                .filter(dm -> {
                    List<DmMember> dmMembers = dmMemberRepository.findAllByDm(dm);

                    List<User> dmUsers = dmMembers.stream() // DmMember로 접근
                            .map(DmMember::getUser) // User 추출
                            .collect(Collectors.toList());
                    return new HashSet<>(dmUsers).equals(new HashSet<>(users));
                })
                .findFirst();
    }

    public List<DmResponseDTO> getDmsByUserId(Long userId) {
        return dmRepository.findAllByUserId(userId).stream()
                .map(dm -> {
                    // DmMembers 조회 및 User 이름 생성
                    String dmName = dmMemberRepository.findAllByDm(dm).stream()
                            .map(dmMember -> dmMember.getUser().getName()) // User의 이름 추출
                            .collect(Collectors.joining(", ")); // 이름들을 ", "로 조인

                    // DmResponseDTO 생성
                    return new DmResponseDTO(dm.getId(), dmName, null);
                })
                .collect(Collectors.toList()); // List<DmResponseDTO>로 변환
    }
    // 디엠 방 단일 조회
    @Transactional(readOnly = true)
    public DmResponseDTO getDmById(Long dmId) {
        Dm dm = dmRepository.findById(dmId)
                .orElseThrow(() -> new RuntimeException("디엠방을 찾을 수 없습니다."));

        // 디엠방의 멤버 이름 조합
        String dmName = dmMemberRepository.findAllByDm(dm).stream()
                .map(dmMember -> dmMember.getUser().getName())
                .collect(Collectors.joining(", "));

        return new DmResponseDTO(dm.getId(), dmName, null);
    }

    // 디엠 멤버 조회
    public List<DmMemberResponseDTO> getDmMembers(Long dmId) {
        Dm dm = dmRepository.findById(dmId)
                .orElseThrow(() -> new RuntimeException("디엠 방을 찾을 수 없습니다"));

        return dmMemberRepository.findAllByDm(dm).stream()
                .map(DmMemberResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public void checkAndDeleteMessage(Long messageId, Long userId) {
        // 메시지 조회
        DmMessage dmMessage = dmMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다."));

        // 본인 메시지만 삭제 가능 확인
        if (!dmMessage.getSender().getId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        // 메시지 삭제
        dmMessageRepository.deleteById(messageId);
    }



}
