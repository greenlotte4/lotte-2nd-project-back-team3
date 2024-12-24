package BackAnt.service;

import BackAnt.dto.RequestDTO.AdminRequestDTO;
import BackAnt.dto.RequestDTO.UserRegisterRequestDTO;
import BackAnt.dto.user.UserDTO;
import BackAnt.entity.Company;
import BackAnt.entity.Department;
import BackAnt.entity.Invite;
import BackAnt.entity.User;
import BackAnt.entity.calendar.Calendar;
import BackAnt.entity.calendar.ViewCalendar;
import BackAnt.entity.enums.Role;
import BackAnt.entity.enums.Status;
import BackAnt.repository.CompanyRepository;
import BackAnt.repository.InviteRepository;
import BackAnt.repository.UserRepository;
import BackAnt.repository.calendar.CalendarRepository;
import BackAnt.repository.calendar.ViewCalendarRepository;
import BackAnt.repository.drive.DriveCollaboratorRepository;
import BackAnt.repository.page.PageCollaboratorRepository;
import BackAnt.repository.project.ProjectCollaboratorRepository;
import BackAnt.repository.project.ProjectTaskAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.io.File;
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
    private final CalendarRepository calendarRepository;
    private final ViewCalendarRepository viewCalendarRepository;
    private final ProjectCollaboratorRepository projectCollaboratorRepository;
    private final PageCollaboratorRepository pageCollaboratorRepository;
    private final DriveCollaboratorRepository driveCollaboratorRepository;
    private final ProjectTaskAssignmentRepository projectTaskAssignmentRepository;
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

        // 입사일 설정 (DTO에서 받아온 값 사용)
        LocalDate joinDate = LocalDate.parse(userDTO.getJoinDate());

        // 총 연차 계산: 입사일이 null이 아닌 경우 계산
        double annualLeaveTotal = 0.0;
        if (joinDate != null) {
            long monthsSinceJoin = ChronoUnit.MONTHS.between(joinDate, LocalDate.now());
            annualLeaveTotal = monthsSinceJoin; // 한 달에 1일 기준
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
                .joinDate(joinDate) // 입사일 저장
                .annualLeaveTotal(annualLeaveTotal) // 계산된 총 연차 저장
                .status(Status.ACTIVE)
                .build();


        User result = userRepository.save(user);

        Calendar calendar = Calendar.builder()
                .name("기본 캘린더")
                .user(result)
                .color("#b2dqff")
                .build();
        Calendar basicCalendar = calendarRepository.save(calendar);

        ViewCalendar viewCalendar = ViewCalendar.builder()
                .user(result)
                .calendar(basicCalendar)
                .build();

        viewCalendarRepository.save(viewCalendar);

        return result;
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
    public Page<UserDTO> getMembersByCompany(Long companyId, int page, int size, String type, String keyword) {
        Pageable pageable = PageRequest.of(page, size);

        // Company 조회
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));


            return userRepository.findAllByCompanyAndStatus(company, Status.valueOf(keyword), pageable)
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

    // 유저 아이디 조회하기
    public String getUserId(String name, String email){
        User user = userRepository.findByNameAndEmail(name, email);
        if(user == null){
            return "해당 정보가 없습니다.";
        }else {
            return user.getUid();
        }
    }

    public String getUserInfo(String uid) {
        log.info("hhhhhhhhhhhhhh"+uid);
        User user = userRepository.findByUid(uid).orElse(null);  // 유저가 없으면 null 반환

        if (user == null) {
            log.info("123");
            return "해당 정보가 없습니다.";  // 유저가 없으면 해당 메시지 반환
        } else {
            log.info("히히히" + user);  // 유저 정보 로그 출력
            return "유저 있음!";  // 유저가 있으면 "유저 있음!" 반환
        }
    }

    public Page<UserDTO> searchUser(String type, String keyword, String companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<User> users = userRepository.findAllByCompany(companyRepository.findById(Long.parseLong(companyId)).orElse(null));
        log.info("흠흠흠흠" + users);
        List<UserDTO> filteredUsers = new ArrayList<>();
        if(Objects.equals(type, "이름")){
            filteredUsers = users.stream()
                    .filter(user ->  user.getName().contains(keyword)) // 조건
                    .map(user -> {UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                        userDTO.setDepartmentName(user.getDepartment().getName());
                        userDTO.setDepartmentId(user.getDepartment().getId());
                        return userDTO; })
                    .toList(); // 필터링 결과를 리스트로 변환
            log.info(filteredUsers);
        }else if(Objects.equals(type, "부서")){
            filteredUsers = users.stream()
                    .filter(user ->  user.getDepartment().getName().contains(keyword)) // 조건
                    .map(user -> {UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                        userDTO.setDepartmentName(user.getDepartment().getName());
                        userDTO.setDepartmentId(user.getDepartment().getId());
                        return userDTO; })
                    .toList(); // 필터링 결과를 리스트로 변환
        }else if(Objects.equals(type, "직급")){
            filteredUsers = users.stream()
                    .filter(user ->  user.getPosition().contains(keyword)) // 조건
                    .map(user -> {UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                        userDTO.setDepartmentName(user.getDepartment().getName());
                        userDTO.setDepartmentId(user.getDepartment().getId());
                        return userDTO; })
                    .toList(); // 필터링 결과를 리스트로 변환
        }else if(Objects.equals(type, "이메일")){
            filteredUsers = users.stream()
                    .filter(user ->  user.getEmail().contains(keyword)) // 조건
                    .map(user -> {UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                        userDTO.setDepartmentName(user.getDepartment().getName());
                        userDTO.setDepartmentId(user.getDepartment().getId());
                        return userDTO; })
                    .toList(); // 필터링 결과를 리스트로 변환
        }
        return new PageImpl<>(filteredUsers, pageable, filteredUsers.size());
    }

    public void updatePosition (List<UserDTO> userDTOS) {
        userDTOS.forEach(userDTO -> {
            User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
            user.setPosition(userDTO.getPosition());
            userRepository.save(user);
        });
    }
    @Transactional
    public void updateStatus (List<String> userIds, String type){
        if(Objects.equals(type, "delete")){
            userIds.forEach(userId -> {
               User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
               log.info(user);
               user.setStatus(Status.DELETED);
               userRepository.save(user);
               calendarRepository.deleteByUser_Uid(user.getUid());
               viewCalendarRepository.deleteByUserId(Long.parseLong(userId));
               projectCollaboratorRepository.deleteByUserId(Long.parseLong(userId));
               pageCollaboratorRepository.deleteByUser_Id(Long.parseLong(userId));
               driveCollaboratorRepository.deleteByUserId(Long.parseLong(userId));
               projectTaskAssignmentRepository.deleteByUserId(Long.parseLong(userId));
            });
        }else if(Objects.equals(type, "password")){
            userIds.forEach(userId -> {
                User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
                user.setPassword(passwordEncoder.encode("a1234@"));
                userRepository.save(user);
            });
        }
    }

}
