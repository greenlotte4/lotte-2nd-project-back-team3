package BackAnt.service;

import BackAnt.dto.RequestDTO.AdminRequestDTO;
import BackAnt.dto.RequestDTO.UserRegisterRequestDTO;
import BackAnt.dto.UserDTO;
import BackAnt.entity.Company;
import BackAnt.entity.Department;
import BackAnt.entity.Invite;
import BackAnt.entity.User;
import BackAnt.entity.enums.Role;
import BackAnt.entity.enums.Status;
import BackAnt.repository.CompanyRepository;
import BackAnt.repository.InviteRepository;
import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private final ImageService imageService;

    // 매퍼 사용 엔티티 - DTO 상호전환
    public UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User toEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    // 아이디 중복확인
    public boolean isIdAvailable(String uid) {
        return !userRepository.existsByUid(uid); // 아이디가 존재하지 않으면 true
    }

    // uid로 객체 찾기
    public User getUserByUid(String uid) {
        return userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("UID에 해당하는 사용자를 찾을 수 없습니다: " + uid));
    }

    // 회원 회원가입
    public User registerUser(UserRegisterRequestDTO userDTO) throws Exception {
        Department department = null;
        // 초대 상태 업데이트
        if (userDTO.getTokenid() != null) {
            Optional<Invite> optionalInvite = inviteRepository.findById(userDTO.getTokenid());

            if (optionalInvite.isPresent()) {
                Invite invite = optionalInvite.get();

                // 초대 상태를 INVITE_COMPLETE로 변경
                invite.setStatus(Status.INVITE_COMPLETE);
                department = invite.getDepartment();
                inviteRepository.save(invite);
            } else {
                throw new IllegalArgumentException("유효하지 않은 초대 토큰입니다.");
            }
        }

        // User 엔티티 생성 및 저장
        User user = User.builder()
                .name(userDTO.getName())
                .uid(userDTO.getUid())
                .password(passwordEncoder.encode(userDTO.getPassword())) // 비밀번호 암호화
                .nick(userDTO.getNick())
                .phoneNumber(userDTO.getPhoneNumber())
                .profileImageUrl(userDTO.getProfileImageUrl())
                .email(userDTO.getEmail())
                .role(Role.valueOf(userDTO.getRole()))
                .position(userDTO.getPosition())
                .company(department.getCompany())
                .department(department)
                .status(Status.ACTIVE)
                .build();

        return userRepository.save(user);
    }

    // 관리자 회원가입
    public User createUser(AdminRequestDTO adminDTO) {
        Company company = companyRepository.findById(adminDTO.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("회사 ID가 잘못되었습니다."));

        User user = modelMapper.map(adminDTO, User.class);
        user.setCompany(company); // 회사 매핑
        user.setPassword(passwordEncoder.encode(adminDTO.getPassword())); // 비밀번호 암호화
        user.setRole(Role.ADMIN);
        user.setPosition("대표이사");
        return userRepository.save(user);
    }


    // 회사별 유저 조회 (페이징)
    public Page<UserDTO> getMembersByCompany(Long companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Company 조회
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));

        // User 엔티티를 UserDTO로 매핑하고 departmentName 설정
        return userRepository.findAllByCompany(company, pageable)
                .map(user -> {
                    UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                    if (user.getDepartment() != null) {
                        userDTO.setDepartmentName(user.getDepartment().getName());
                        userDTO.setDepartmentId(user.getDepartment().getId());
                    }
                    return userDTO;
                });
    }

    public List<UserDTO> getAllMembers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            if (user.getDepartment() != null) {
                userDTO.setDepartmentName(user.getDepartment().getName());
                userDTO.setDepartmentId(user.getDepartment().getId());
            }
            return userDTO;
        }).toList();
    }

    public List<UserDTO> getAllMembersByCompany(Long companyId) {
        // Company 조회
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));

        // 회사별 모든 사용자 조회
        List<User> users = userRepository.findAllByCompany(company);

        // Entity -> DTO 변환
        return users.stream().map(user -> {
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            if (user.getDepartment() != null) {
                userDTO.setDepartmentName(user.getDepartment().getName());
                userDTO.setDepartmentId(user.getDepartment().getId());
            }
            return userDTO;
        }).toList();
    }

    // 부서별 사용자 조회
    public List<UserDTO> getUsersByDepartmentId(Long departmentId) {
        List<User> users = userRepository.findByDepartmentId(departmentId);

        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }


    public void updateUserInfo(String info, String uid, String type){
        User user = userRepository.findByUid(uid).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        if(Objects.equals(type, "name")){
            user.updateName(info);
        }else if(Objects.equals(type, "email")){
            user.updateEmail(info);
        }else if(Objects.equals(type, "phoneNumber")){
            user.updatePhoneNumber(info);
        }
        userRepository.save(user);
    }

    public void updateUserProfile(String uid, MultipartFile profileImage){

        log.info("1231231233333333333333333333333");

        User user = userRepository.findByUid(uid).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        log.info(user.getProfileImageUrl());

        File oldFile = new File(user.getProfileImageUrl());
        if (oldFile.exists()) {
            boolean result = oldFile.delete();
            if (result){
                log.info("profile 이미지가 교체되었습니다.");
            }
        }
        try {
            // 이미지 업로드 처리
            if (profileImage != null && !profileImage.isEmpty()) {
                String imageUrl = imageService.uploadImage(profileImage);
                log.info(imageUrl);
                user.updateProfileImageUrl(imageUrl);
                userRepository.save(user);
            }
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public boolean passUpdateCheck (String uid , String pass, String type) {

        User user = userRepository.findByUid(uid).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        if(Objects.equals(type, "check")){
            return passwordEncoder.matches(pass, user.getPassword());
        } else if (Objects.equals(type, "update")){
            user.setPassword(passwordEncoder.encode(pass));
            userRepository.save(user);
            return true;
        }

        return false;
    }

    // 회사 대표이사 조회
    // BoardService.java 또는 해당 서비스 클래스
    public List<User> getUsersByCompanyAndPosition(Long companyId, String position) {
        // 회사 ID로 회사 객체 조회
        Optional<Company> company = companyRepository.findById(companyId);

        if (company.isPresent()) {
            // Company 객체와 Position으로 사용자 조회
            return userRepository.findByCompanyAndPosition(company.get(), position);
        }

        // 회사가 없을 경우 빈 리스트 반환
        return Collections.emptyList();
    }




}
