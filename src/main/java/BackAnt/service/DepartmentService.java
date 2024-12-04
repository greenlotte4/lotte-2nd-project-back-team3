package BackAnt.service;

import BackAnt.dto.DepartmentDTO;
import BackAnt.entity.Company;
import BackAnt.entity.Department;
import BackAnt.repository.CompanyRepository;
import BackAnt.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

        List<Department> departments = departmentRepository.findByCompanyId(companyId);

        List<DepartmentDTO> departmentDTOS = departments.stream()
                .map(department -> {
                    DepartmentDTO dto = new DepartmentDTO();
                    dto.setId(department.getId());
                    dto.setName(department.getName());
                    return dto;
                })
                .collect(Collectors.toList());

        return departmentDTOS;
    }

}
