package BackAnt.service;

import BackAnt.dto.RequestDTO.AdminRequestDTO;
import BackAnt.dto.UserDTO;
import BackAnt.entity.Company;
import BackAnt.entity.Invite;
import BackAnt.entity.User;
import BackAnt.entity.enums.Status;
import BackAnt.repository.CompanyRepository;
import BackAnt.repository.InviteRepository;
import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/*
    날짜 : 2024/11/29
    이름 : 최준혁
    내용 : 유저 서비스 생성
*/

@RequiredArgsConstructor
@Log4j2
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final InviteRepository inviteRepository;

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    private final InviteService inviteService;


    // 매퍼 사용 엔티티 - DTO 상호전환
    public UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User toEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    // 회원 회원가입
    public void registerUser(String inviteToken, UserDTO userDTO) throws Exception {
        User user = inviteService.validateInvite(inviteToken);

        user.setPassword(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setStatus(Status.ACTIVE); // 상태를 활성화로 변경
        userRepository.save(user);

        // 초대 상태를 만료로 변경
        Invite invite = inviteRepository.findByInviteToken(inviteToken).orElseThrow();
        invite.setStatus(Status.EXPIRED);
        inviteRepository.save(invite);
    }

    // 관리자 회원가입
    public User createUser(AdminRequestDTO adminDTO) {
        Company company = companyRepository.findById(adminDTO.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("회사 ID가 잘못되었습니다."));

        User user = modelMapper.map(adminDTO, User.class);
        user.setCompany(company); // 회사 매핑
        user.setPassword(passwordEncoder.encode(adminDTO.getPassword())); // 비밀번호 암호화
        return userRepository.save(user);
    }




}
