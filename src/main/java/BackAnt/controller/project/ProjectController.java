package BackAnt.controller.project;

import BackAnt.dto.project.ProjectDTO;
import BackAnt.repository.project.ProjectRepository;
import BackAnt.repository.UserRepository;
import BackAnt.service.project.ProjectCollaboratorService;
import BackAnt.service.project.ProjectService;
import BackAnt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    날 짜 : 2024/12/2(월)
    담당자 : 강은경
    내 용 : Project 를 위한 Controller 생성
*/

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ProjectRepository projectRepository;
    private final ProjectCollaboratorService projectCollaboratorService;
    private final UserRepository userRepository;

    // 프로젝트 저장
    @PostMapping("/add")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {

        // 현재 로그인한 사용자 ID 가져오기
        String uid = projectDTO.getUid();
        log.info("uid : " + uid);


        ProjectDTO savedProjectDTO = projectService.createProject(projectDTO, uid);
        log.info("savedProjectDTO " + savedProjectDTO);

        return ResponseEntity.ok(savedProjectDTO);
    }

    // 내 프로젝트 조회
    @GetMapping("/list/{uid}")
    public ResponseEntity<List<ProjectDTO>> getMyProjects(@PathVariable String uid) {
        log.info("Received uid: " + uid);

        List<ProjectDTO> myProjects = projectService.getMyProjects(uid);
        return ResponseEntity.ok(myProjects);
    }

    // 프로젝트 상세 페이지
    @GetMapping("/view/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        log.info("id: " + id);
        ProjectDTO project = projectService.getProjectById(id);
        log.info("project: " + project);
        return ResponseEntity.ok(project);
    }

    // 프로젝트 수정
    @PutMapping("/update/{projectId}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long projectId, @RequestBody ProjectDTO projectDTO) {
        log.info("projectId: " + projectId);
        log.info("projectDTO: " + projectDTO);

        ProjectDTO updatedProject = projectService.updateProject(projectId, projectDTO);
        log.info("updatedProject: " + updatedProject);

        return ResponseEntity.ok(updatedProject);
    }

    // 프로젝트 삭제
    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {

        log.info("삭제할 projectId: " + projectId);

        try {
            projectService.deleteProject(projectId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting project: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    // 프로젝트 상태 변경(진행중/완료)
    @PutMapping("update/status/{projectId}")
    public ResponseEntity<ProjectDTO> updateProjectStatus(@PathVariable Long projectId, @RequestParam int status) {
        log.info("projectId: " + projectId);
        log.info("status: " + status);

        ProjectDTO updatedProject = projectService.updateProjectStatus(projectId, status);
        return ResponseEntity.ok(updatedProject);

    }

    // 해당 사용자가 등록한 프로젝트 수 반환
    @GetMapping("count/{uid}")
    public ResponseEntity<Integer> getUserProjectCount(@PathVariable String uid) {

        log.info("프로젝트 수 조회 uid: " + uid);

        // 사용자 uid에 대한 프로젝트 수 조회
        int projectCount = projectService.countProjectsByUid(uid);
        log.info("projectCount: " + projectCount);


        // 프로젝트 수를 반환
        return ResponseEntity.ok(projectCount); // HTTP 200 OK와 함께 프로젝트 수 반환
    }


}