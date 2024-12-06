package BackAnt.service;

import BackAnt.dto.DepartmentDTO;
import BackAnt.dto.UserDTO;
import BackAnt.entity.Company;
import BackAnt.entity.Department;
import BackAnt.entity.User;
import BackAnt.repository.CompanyRepository;
import BackAnt.repository.DepartmentRepository;
import BackAnt.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Console;
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
    // 회사별 부서 목록 조회
    public List<DepartmentDTO> getDepartmentsByCompanyId(Long companyId) {

        // 회사 ID로 부서 목록 조회
        List<Department> departments = departmentRepository.findByCompanyId(companyId);

        // 부서별 DTO 생성 및 유저 목록 추가
        List<DepartmentDTO> departmentDTOS = departments.stream()
                .map(department -> {
                    DepartmentDTO dto = new DepartmentDTO();
                    dto.setId(department.getId());
                    dto.setName(department.getName());

                    // 부서에 속한 유저 목록 가져오기
                    List<UserDTO> userDTOS = department.getUsers().stream()
                            .map(user -> {
                                UserDTO userDTO = new UserDTO();
                                userDTO.setId(user.getId());
                                userDTO.setName(user.getName());
                                userDTO.setEmail(user.getEmail());
                                userDTO.setPosition(user.getPosition());
                                return userDTO;
                            })
                            .collect(Collectors.toList());

                    dto.setUsers(userDTOS); // DTO에 유저 목록 설정
                    return dto;
                })
                .collect(Collectors.toList());

        return departmentDTOS; // 최종 DTO 리스트 반환
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

}
