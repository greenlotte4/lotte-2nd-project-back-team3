package BackAnt.service;
/*
    날짜 : 2024/12/03
    이름 : 최준혁
    내용 : 초대 서비스 생성
*/

import BackAnt.dto.UserDTO;
import BackAnt.entity.Invite;
import BackAnt.entity.User;
import BackAnt.entity.enums.Status;
import BackAnt.repository.InviteRepository;
import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Log4j2
@Service
public class InviteService {
    private final InviteRepository inviteRepository;
    private final UserRepository userRepository;

    // 멤버 초대 생성
    public String createInvite(UserDTO userDTO) {
        // User 엔티티 생성
        User user = User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .status(Status.INVITE)
                .build();

        // 사용자 저장
        userRepository.save(user);

        // 초대 엔티티 생성
        String inviteToken = UUID.randomUUID().toString();
        Invite invite = Invite.builder()
                .inviteToken(inviteToken)
                .expiry(LocalDateTime.now().plusDays(7)) // 초대 만료 시간 (7일)
                .status(Status.INVITE)
                .user(user)
                .build();

        inviteRepository.save(invite);

        return inviteToken;
    }


    // 멤버 초대 검증
    public User validateInvite(String inviteToken) throws Exception {
        Invite invite = inviteRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new Exception("유효하지 않은 초대 토큰입니다."));

        if (invite.getExpiry().isBefore(LocalDateTime.now())) {
            invite.setStatus(Status.EXPIRED);
            inviteRepository.save(invite);
            throw new Exception("초대 토큰이 만료되었습니다.");
        }

        return invite.getUser();
    }

}
