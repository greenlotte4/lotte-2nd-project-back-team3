package BackAnt.controller.project;

import BackAnt.dto.project.ProjectStateDTO;
import BackAnt.service.project.ProjectStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    날 짜 : 2024/12/4(수)
    담당자 : 강은경
    내 용 : ProjectState 를 위한 Controller 생성
*/

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/project/state")
public class ProjectStateController {

    private final ProjectStateService projectStateService;

    // 프로젝트 상태 등록
    @PostMapping("/insert")
    public ResponseEntity<ProjectStateDTO> addState(@RequestBody ProjectStateDTO projectStateDTO) {
        ProjectStateDTO savedState = projectStateService.addState(projectStateDTO);
        return ResponseEntity.ok(savedState);
    }

    // 프로젝트별 상태 가져오기
    @GetMapping("/select/{id}")
    public ResponseEntity<List<ProjectStateDTO>> getAllStatesByProject(@PathVariable Long id) {

        log.info("id : " + id);

        try {
            List<ProjectStateDTO> states = projectStateService.getAllStatesByProjectId(id);
            return ResponseEntity.ok(states);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 프로젝트 작업상태 수정
    @PutMapping("/update/{stateId}")
    public ResponseEntity<ProjectStateDTO> updateState(
            @PathVariable Long stateId,
            @RequestBody ProjectStateDTO projectStateDTO) {

        log.info("stateId : " + stateId);
        log.info("projectStateDTO : " + projectStateDTO);

        try {
            ProjectStateDTO updatedState = projectStateService.updateState(stateId, projectStateDTO);
            return ResponseEntity.ok(updatedState);
        } catch (IllegalArgumentException e) {
            log.error("State update failed: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Internal Server Error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 프로젝트 작업상태 삭제
    @DeleteMapping("/delete/{stateId}")
    public ResponseEntity<Void> deleteState(@PathVariable Long stateId) {

        log.info("stateId : " + stateId);

        try {
            projectStateService.deleteState(stateId);
            return ResponseEntity.noContent().build(); // 성공 시 204 No Content 반환
        } catch (Exception e) {
            log.error("Error deleting state: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}