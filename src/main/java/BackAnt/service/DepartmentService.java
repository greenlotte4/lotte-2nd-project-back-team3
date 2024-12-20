package BackAnt.service;

import BackAnt.dto.DepartmentDTO;
import BackAnt.dto.user.UserDTO;
import BackAnt.entity.Company;
import BackAnt.entity.Department;
import BackAnt.entity.Invite;
import BackAnt.entity.User;
import BackAnt.repository.CompanyRepository;
import BackAnt.repository.DepartmentRepository;
import BackAnt.repository.InviteRepository;
import BackAnt.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class DepartmentService {

    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final InviteRepository inviteRepository;

    // 회사별 부서 생성
    public Department insertDepartment(DepartmentDTO departmentDTO) {
        // 회사 ID를 기반으로 회사 엔티티를 조회
        Optional<Company> optionalCompany = companyRepository.findById(departmentDTO.getCompany_id());

        if (optionalCompany.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 회사 ID입니다.");
        }

        Company company = optionalCompany.get();

        log.info("컴퍼니" + company);
        // DTO를 엔티티로 변환
        Department department = modelMapper.map(departmentDTO, Department.class);

        department.setCompany(company);
        log.info("부서" + department.getCompany());
        // 부서 엔티티 저장
        return departmentRepository.save(department);
    }

    // 회사별 부서 목록 조회
    public List<DepartmentDTO> getDepartmentsByCompanyId(Long companyId) {

        // 회사 ID로 부서 목록 조회
        List<Department> departments = departmentRepository.findByCompanyId(companyId);

        // 모든 유저 조회: 부서가 null인 유저도 가져오기
        List<User> allUsers = userRepository.findByCompanyId(companyId);

        // 부서별 DTO 생성 및 유저 목록 추가
        List<DepartmentDTO> departmentDTOS = departments.stream()
                .map(department -> {
                    DepartmentDTO dto = new DepartmentDTO();
                    dto.setId(department.getId());
                    dto.setName(department.getName());

                    // 부서에 속한 유저 목록 가져오기
                    List<UserDTO> userDTOS = department.getUsers().stream()
                            .map(user -> mapUserToDTO(user))
                            .collect(Collectors.toList());

                    dto.setUsers(userDTOS); // DTO에 유저 목록 설정
                    return dto;
                })
                .collect(Collectors.toList());

        // 부서가 지정되지 않은 사용자 목록 (department == null)
        List<UserDTO> unassignedUsers = allUsers.stream()
                .filter(user -> user.getDepartment() == null)
                .map(user -> mapUserToDTO(user))
                .collect(Collectors.toList());

        if (!unassignedUsers.isEmpty()) {
            // "미지정 사용자"라는 가상의 부서 추가
            DepartmentDTO unassignedDept = new DepartmentDTO();
            unassignedDept.setId(0L); // ID를 0으로 설정
            unassignedDept.setName("부서 미지정");
            unassignedDept.setUsers(unassignedUsers);

            departmentDTOS.add(unassignedDept);
        }

        return departmentDTOS; // 최종 DTO 리스트 반환
    }
    private UserDTO mapUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPosition(user.getPosition());
        return userDTO;
    }

    public List<String> selectDepart (String depart) {
        Long departNo = Long.parseLong(depart);

        Department department = departmentRepository.findById(departNo).orElseThrow(() -> new EntityNotFoundException("이 id의 department가 없습니다."));

        List<User> users = department.getUsers();

        return users.stream().map(User::getName).toList();

    }

    // 이름 수정 메서드
    public DepartmentDTO updateDepartmentName(Long id, String newName) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        department.setName(newName); // 부서 이름 업데이트
        Department updatedDepartment = departmentRepository.save(department);

        // ModelMapper를 사용해 엔티티를 DTO로 변환
        return modelMapper.map(updatedDepartment, DepartmentDTO.class);
    }

    // 유저 부서 이동
    @Transactional
    public void moveUserToDepartment(Long userId, Long departmentId) {
        // 사용자 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // 새 부서 찾기
        Department newDepartment = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없습니다."));

        // 사용자 부서 업데이트
        user.setDepartment(newDepartment);
        userRepository.save(user);
    }

    // 부서 삭제
    @Transactional
    public void deleteDepartment(Long departmentId, Long targetDepartmentId) {
        // 삭제할 부서 찾기
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없습니다."));

        // 1. 부서에 속한 초대(Invite) 처리
        List<Invite> invites = inviteRepository.findByDepartmentId(departmentId);

        if (!invites.isEmpty()) {
            if (targetDepartmentId != null) {
                // 초대를 다른 부서로 이동
                Department targetDepartment = departmentRepository.findById(targetDepartmentId)
                        .orElseThrow(() -> new IllegalArgumentException("이동할 부서를 찾을 수 없습니다."));
                invites.forEach(invite -> invite.setDepartment(targetDepartment));
            } else {
                // 초대의 부서 참조를 제거
                invites.forEach(invite -> invite.setDepartment(null));
            }
            inviteRepository.saveAll(invites);
        }

        // 2. 부서에 속한 유저 처리
        List<User> users = userRepository.findByDepartmentId(departmentId);

        if (!users.isEmpty()) {
            if (targetDepartmentId != null) {
                // 유저를 다른 부서로 이동
                Department targetDepartment = departmentRepository.findById(targetDepartmentId)
                        .orElseThrow(() -> new IllegalArgumentException("이동할 부서를 찾을 수 없습니다."));
                users.forEach(user -> user.setDepartment(targetDepartment));
            } else {
                // 유저를 '부서 미지정' 상태로 설정
                users.forEach(user -> user.setDepartment(null));
            }
            userRepository.saveAll(users);
        }

        // 3. 부서 삭제
        departmentRepository.delete(department);
    }


}
