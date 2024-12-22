package BackAnt.controller.project;

import BackAnt.dto.project.ProjectTaskDTO;
import BackAnt.repository.project.ProjectTaskAssignmentRepository;
import BackAnt.repository.project.ProjectTaskRepository;
import BackAnt.service.project.ProjectTaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
    날 짜 : 2024/12/4(수)
    담당자 : 강은경
    내 용 : ProjectTask 를 위한 Controller 생성
*/

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/project/task")
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;
    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectTaskAssignmentRepository projectTaskAssignmentRepository;


    // 프로젝트 작업 등록
    @PostMapping("/insert")
    public ResponseEntity<ProjectTaskDTO> createTask(@RequestBody ProjectTaskDTO projectTaskDTO) {

        log.info("projectTaskDTO : " + projectTaskDTO);

        ProjectTaskDTO createdTask = projectTaskService.createTask(projectTaskDTO);
        log.info("createdTask : " + createdTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    // 작업상태별 작업 select
    @GetMapping("/select/{stateId}")
    public ResponseEntity<List<ProjectTaskDTO>> getTasksByStateId(@PathVariable Long stateId) {
        log.info("stateId : " + stateId);

        List<ProjectTaskDTO> tasks = projectTaskService.getTasksWithAssignedUsers(stateId);
        log.info("tasks : " + tasks);
        return ResponseEntity.ok(tasks);
    }

    // 작업 수정
    @PutMapping("/update/{taskId}")
    public ResponseEntity<ProjectTaskDTO> updateTask(@PathVariable Long taskId, @RequestBody ProjectTaskDTO projectTaskDTO) {
        log.info("taskId : " + taskId);
        log.info("projectTaskDTO : " + projectTaskDTO);

        ProjectTaskDTO updatedTask = projectTaskService.updateTask(taskId, projectTaskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    // 작업 개별 삭제
    @Transactional
    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long taskId) {
        log.info("taskId : " + taskId);

        projectTaskService.deleteTaskById(taskId);

        return ResponseEntity.noContent().build();
    }


    // 작업 드래그앤드랍시 변경된 위치와 상태를 업데이트
    @PutMapping("/updatePosition/{taskId}")
    public ResponseEntity<ProjectTaskDTO> updateTaskPosition(
            @PathVariable Long taskId,
            @RequestBody Map<String, Long> request) {

        Long newStateId = request.get("stateId");
        int newPosition = request.get("position").intValue();

        log.info("taskId: " + taskId + "newStateId : " + newStateId + "newPosition : " + newPosition);

        ProjectTaskDTO updatedTask = projectTaskService.updateTaskState(taskId, newStateId, newPosition);
        log.info("updatedTask: ", updatedTask);

        return ResponseEntity.ok(updatedTask);
    }

}