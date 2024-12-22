package BackAnt.controller.project;

import BackAnt.dto.project.UserForProjectDTO;
import BackAnt.entity.User;
import BackAnt.entity.project.Project;
import BackAnt.repository.project.ProjectCollaboratorRepository;
import BackAnt.repository.project.ProjectRepository;
import BackAnt.repository.UserRepository;
import BackAnt.service.project.ProjectCollaboratorService;
import BackAnt.service.project.ProjectService;
import BackAnt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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
@RequestMapping("/api/project/collaborator")
public class ProjectCollaboratorController {

    private final ProjectService projectService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectCollaboratorRepository projectCollaboratorRepository;
    private final ProjectCollaboratorService projectCollaboratorService;

    // 프로젝트별 협업자 추가
    @PostMapping("/insert/{projectId}/{id}")
    public ResponseEntity<?> addCollaboratorToProject(@PathVariable Long projectId, @RequestBody List<Long> userIds, @PathVariable Long id) {
        log.info("projectId: " + projectId);
        log.info("userIds: " + userIds);
        log.info("id: " + id);

        // 프로젝트 id별 project 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        log.info("project: " + project);


        // 프론트에서 선택된 협업자 추가
         for(Long userId : userIds) {
            User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            projectCollaboratorService.addCollaborator(project, user, 1, id);
        }

            return ResponseEntity.ok("협업자가 성공적으로 추가되었습니다.");
        }


        // 프로젝트별 등록된 협업자 조회
        @GetMapping("/select/{projectId}")
        public ResponseEntity<List<UserForProjectDTO>> getProjectCollaborators(@PathVariable Long projectId) {
            log.info("projectId: " + projectId);

            List<UserForProjectDTO> collaborators = projectCollaboratorService.getUsersByProjectId(projectId);
            log.info("userForProjectDTO 반환되는 collaborators: " + collaborators);

            return ResponseEntity.ok(collaborators);

        }

        // 프로젝트 id와 사용자 id를 기준으로 협업자 삭제
        @DeleteMapping("/delete/{projectId}/{userId}")
        public ResponseEntity<String> removeProjectCollaborator(@PathVariable Long projectId, @PathVariable Long userId) {
            log.info("projectId: " + projectId);
            log.info("userId: " + userId);

            projectCollaboratorService.deleteCollaborator(projectId, userId);

            return ResponseEntity.ok("협업자가 성공적으로 삭제되었습니다.");
        }

        // 프로젝트 id에 따른 협업자 수 세는 메서드
        @GetMapping("/count/{projectId}")
        public ResponseEntity<Integer> getCollaboratorCount(@PathVariable Long projectId) {
            log.info("projectId: " + projectId);

            int collaboratorCount = projectCollaboratorService.countCollaboratorsByProjectId(projectId);
            return ResponseEntity.ok(collaboratorCount);
        }


}