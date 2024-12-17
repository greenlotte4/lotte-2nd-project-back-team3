package BackAnt.controller.department;

import BackAnt.dto.DepartmentDTO;
import BackAnt.dto.RequestDTO.UpdateDepartmentNameRequestDTO;
import BackAnt.entity.Department;
import BackAnt.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    // 부서 이름 수정
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartmentName(
            @PathVariable Long id,
            @RequestBody UpdateDepartmentNameRequestDTO request) {
        DepartmentDTO updatedDepartment = departmentService.updateDepartmentName(id, request.getName());
        return ResponseEntity.ok(updatedDepartment);
    }


    // 유저 부서 이동
    @PatchMapping("/move-user/{userId}")
    public ResponseEntity<String> moveUserToDepartment(
            @PathVariable Long userId,
            @RequestBody Map<String, Long> request
    ) {
        Long departmentId = request.get("departmentId");
        if (departmentId == null) {
            return ResponseEntity.badRequest().body("Department ID is required");
        }

        try {
            log.info("user" + userId + " departmentId" + departmentId);
            departmentService.moveUserToDepartment(userId, departmentId);
            return ResponseEntity.ok("사용자가 성공적으로 부서로 이동되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사용자 부서 이동 중 오류가 발생했습니다.");
        }
    }

    // 부서 삭제
    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Map<String, String>> deleteDepartment(
            @PathVariable Long departmentId,
            @RequestBody(required = false) Map<String, Long> request
    ) {
        Long targetDepartmentId = request != null ? request.get("targetDepartmentId") : null;

        try {
            log.info("Deleting department with ID: " + departmentId);
            log.info("Moving users to department ID: " + targetDepartmentId);

            departmentService.deleteDepartment(departmentId, targetDepartmentId);

            // 성공 메시지 반환
            Map<String, String> response = Map.of("message", "부서가 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("부서 삭제 실패: 잘못된 입력 - {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    Map.of("error", "부서 삭제 실패", "details", e.getMessage())
            );

        } catch (Exception e) {
            log.error("부서 삭제 실패: 시스템 오류 - ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "부서 삭제 중 오류가 발생했습니다.", "details", e.getMessage())
            );
        }
    }



}
