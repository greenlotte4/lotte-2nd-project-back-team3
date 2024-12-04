package BackAnt.controller.department;

import BackAnt.dto.DepartmentDTO;
import BackAnt.entity.Department;
import BackAnt.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    // 회사별 부서 생성
    @PostMapping("/insert")
    public ResponseEntity<Department> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        log.info("들어오니 부서?");
        log.info("부서" + departmentDTO.toString());
        Department createdDepartment = departmentService.insertDepartment(departmentDTO);
        return ResponseEntity.ok(createdDepartment);
    }
    // 회사별 부서 조회
    @GetMapping("/byCompany/{companyId}")
    public ResponseEntity<List<DepartmentDTO>> getDepartmentsByCompanyId(@PathVariable Long companyId) {
        log.info("들어오니? 부서조회?");
        List<DepartmentDTO> departments = departmentService.getDepartmentsByCompanyId(companyId);
        log.info("나오니? 부서조회?" + departments.toString());
        return ResponseEntity.ok(departments);
    }

}
