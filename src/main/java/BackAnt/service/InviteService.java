package BackAnt.service;
/*
    날짜 : 2024/12/03
    이름 : 최준혁
    내용 : 초대 서비스 생성
*/

import BackAnt.dto.RequestDTO.InviteRequestDTO;
import BackAnt.entity.Department;
import BackAnt.entity.Invite;
import BackAnt.entity.enums.Role;
import BackAnt.entity.enums.Status;
import BackAnt.repository.DepartmentRepository;
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
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;

    // 멤버 초대 생성
    public String createInvite(InviteRequestDTO inviteRequestDTO) {
        // 부서 확인
        Department department = departmentRepository.findById(inviteRequestDTO.getDepartment())
                .orElseThrow(() -> new IllegalArgumentException("부서를 찾을 수 없습니다."));

        // 초대 토큰 생성
        String inviteToken = UUID.randomUUID().toString();

        // Invite 엔티티 생성
        Invite invite = Invite.builder()
                .email(inviteRequestDTO.getEmail())
                .name(inviteRequestDTO.getName())
                .position(inviteRequestDTO.getPosition())
                .phoneNumber(inviteRequestDTO.getPhoneNumber())
                .department(department)
                .role(Role.valueOf(inviteRequestDTO.getRole()))
                .inviteToken(inviteToken)
                .expiry(LocalDateTime.now().plusDays(7)) // 7일 후 만료
                .status(Status.INVITE)
                .note(inviteRequestDTO.getNote())
                .build();

        // 저장
        inviteRepository.save(invite);

        // 초대 이메일 전송
        emailService.sendInviteEmailWithTemplate(
                invite.getEmail(),
                invite.getName(),
                department.getName(),
                invite.getInviteToken()
        );

        return inviteToken;
    }


    // 멤버 초대 검증
    public Invite verifyToken(String token) {
        // 토큰에 해당하는 초대 정보 찾기
        Invite invite = inviteRepository.findByInviteToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 토큰입니다."));

        // 초대 상태 확인
        if (invite.getExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("초대 토큰이 만료되었습니다.");
        }

        if (invite.getStatus() != Status.INVITE) {
            throw new IllegalArgumentException("초대 토큰이 이미 사용되었습니다.");
        }

        return invite; // 유효한 초대 반환
    }

}
