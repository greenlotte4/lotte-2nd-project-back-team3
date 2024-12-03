package BackAnt.service;

import BackAnt.dto.DepartmentDTO;
import BackAnt.entity.Company;
import BackAnt.entity.Department;
import BackAnt.repository.CompanyRepository;
import BackAnt.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Service
public class DepartmentService {

    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;

    // 부서 생성
    public Department insertDepartment(DepartmentDTO departmentDTO) {
        // 회사 ID를 기반으로 회사 엔티티를 조회
        Optional<Company> optionalCompany = companyRepository.findById(departmentDTO.getCompany_id());

        if (optionalCompany.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 회사 ID입니다.");
        }

        Company company = optionalCompany.get();

        // DTO를 엔티티로 변환
        Department department = Department.builder()
                .name(departmentDTO.getName())
                .company(company) // 회사 엔티티 설정
                .build();

        // 부서 엔티티 저장
        return departmentRepository.save(department);
    }
}
