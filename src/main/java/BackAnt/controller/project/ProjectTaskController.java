package BackAnt.controller.project;

import BackAnt.dto.ProjectStateDTO;
import BackAnt.dto.ProjectTaskDTO;
import BackAnt.service.ProjectStateService;
import BackAnt.service.ProjectTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    // 프로젝트 작업 등록
    @PostMapping("/insert")
    public ResponseEntity<ProjectTaskDTO> createTask(@RequestBody ProjectTaskDTO projectTaskDTO) {

        log.info("projectTaskDTO : " + projectTaskDTO);

        ProjectTaskDTO createdTask = projectTaskService.createTask(projectTaskDTO);
        log.info("createdTask : " + createdTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/select/{stateId}")
    public ResponseEntity<List<ProjectTaskDTO>> getTasksByStateId(@PathVariable Long stateId) {
        log.info("stateId : " + stateId);

        List<ProjectTaskDTO> tasks = projectTaskService.getTasksByStateId(stateId);
        log.info("tasks : " + tasks);
        return ResponseEntity.ok(tasks);
    }




}