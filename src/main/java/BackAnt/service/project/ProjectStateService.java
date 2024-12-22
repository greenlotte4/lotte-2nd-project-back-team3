package BackAnt.service.project;

import BackAnt.dto.project.ProjectStateDTO;
import BackAnt.entity.project.Project;
import BackAnt.entity.project.ProjectCollaborator;
import BackAnt.entity.project.ProjectState;
import BackAnt.entity.project.ProjectTask;
import BackAnt.repository.project.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
    날 짜 : 2024/12/2(월)
    담당자 : 강은경
    내 용 : ProjectState 를 위한 Service 생성
*/

@RequiredArgsConstructor
@Service
@Log4j2
public class ProjectStateService {
    private final ProjectRepository projectRepository;
    private final ProjectStateRepository projectStateRepository;
    private final ModelMapper modelMapper;
    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectTaskAssignmentRepository projectTaskAssignmentRepository;
    private final ProjectCollaboratorRepository projectCollaboratorRepository;
    private final SimpMessagingTemplate messagingTemplate;


    // 프로젝트 상태 등록
    public ProjectStateDTO addState(ProjectStateDTO projectStateDTO) {
        // 프로젝트 조회
        long ProjectId = projectStateDTO.getProjectId();
        Project project = projectRepository.findById(ProjectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // dto -> 엔티티 변환
        ProjectState projectState = modelMapper.map(projectStateDTO, ProjectState.class);
        projectState.setProject(project); // 프로젝트 설정

        // 엔티티 저장
        ProjectState savedState = projectStateRepository.save(projectState);

        ProjectStateDTO dto = modelMapper.map(savedState, ProjectStateDTO.class);
        dto.setAction("stateInsert");
        dto.setProjectId(ProjectId);

        log.info("웹소켓 쏘기 전 프로젝트 상태 등록 dto : " + dto);

        // 웹소켓을 쏴주기 위한 프로젝트 id에 따른 협업자 조회
        List<ProjectCollaborator> projectCollaborators = projectCollaboratorRepository.findByProject_Id(project.getId());
        log.info("projectCollaborators : " + projectCollaborators);

        // 2. WebSocket을 통한 실시간 알림 전송
        projectCollaborators.forEach(projectCollaborator -> {
            String destination = "/topic/project/" + projectCollaborator.getUser().getId();
            log.info("경로" + destination);
            messagingTemplate.convertAndSend(destination, dto);
        });

        return dto;
    }


    // 프로젝트별 상태 가져오기
    public List<ProjectStateDTO> getAllStatesByProjectId(Long id) {
        List<ProjectState> states = projectStateRepository.findAllByProjectId(id);
        log.info("states : " + states);

        return states.stream()
                .map(state -> modelMapper.map(state, ProjectStateDTO.class))
                .collect(Collectors.toList());
    }

    // 프로젝트 작업상태 수정
    public ProjectStateDTO updateState(Long stateId, ProjectStateDTO projectStateDTO) {

        // 상태 조회
        ProjectState existingState = projectStateRepository.findById(stateId)
                .orElseThrow(() -> new IllegalArgumentException("State not found"));

        // 업데이트할 필드 설정
        existingState.setTitle(projectStateDTO.getTitle());
        existingState.setDescription(projectStateDTO.getDescription());
        existingState.setColor(projectStateDTO.getColor());

        // 저장
        ProjectState updatedState = projectStateRepository.save(existingState);
        log.info("updatedState : " + updatedState);

        ProjectStateDTO dto = modelMapper.map(updatedState, ProjectStateDTO.class);
        dto.setAction("stateUpdate");
        dto.setProjectId(projectStateDTO.getProjectId());
        log.info("dto : " + dto);

        // 웹소켓을 쏴주기 위한 프로젝트 id에 따른 협업자 조회
        List<ProjectCollaborator> projectCollaborators = projectCollaboratorRepository.findByProject_Id(updatedState.getProject().getId());
        log.info("projectCollaborators : " + projectCollaborators);

        // 2. WebSocket을 통한 실시간 알림 전송
        projectCollaborators.forEach(projectCollaborator -> {
            String destination = "/topic/project/" + projectCollaborator.getUser().getId();
            log.info("경로" + destination);
            messagingTemplate.convertAndSend(destination, dto);
        });

        return dto;


    }

    // 프로젝트 작업 상태 삭제
    @Transactional
    public void deleteState(Long stateId) {

        // 해당 작업 상태 조회
        ProjectState projectState = projectStateRepository.findById(stateId)
                .orElseThrow(() -> new IllegalArgumentException("해당 작업 상태가 존재하지 않습니다."));

        // 해당 작업상태에 속한 모든 작업 조회
        List<ProjectTask> tasks = projectTaskRepository.findAllByStateId(stateId);

        // 작업과 관련된 작업담당자 삭제
        for (ProjectTask task : tasks) {
            projectTaskAssignmentRepository.deleteByTaskId(task.getId());  // 해당 작업에 대한 작업담당자 삭제
        }

        // 작업 삭제
        projectTaskRepository.deleteAll(tasks);

        // 작업 상태 삭제
        projectStateRepository.deleteById(stateId);

        // 해당 작업 상태의 프로젝트 정보 가져오기
        Project project = projectState.getProject();
        log.info("project : " + project);

        ProjectStateDTO dto = modelMapper.map(projectState, ProjectStateDTO.class);
        dto.setAction("stateDelete");
        dto.setProjectId(project.getId());
        log.info("dto : " + dto);

        // 웹소켓을 쏴주기 위한 프로젝트 id에 따른 협업자 조회
        List<ProjectCollaborator> projectCollaborators = projectCollaboratorRepository.findByProject_Id(project.getId());
        log.info("projectCollaborators : " + projectCollaborators);

        // 2. WebSocket을 통한 실시간 알림 전송
        projectCollaborators.forEach(projectCollaborator -> {
            String destination = "/topic/project/" + projectCollaborator.getUser().getId();
            log.info("경로" + destination);
            messagingTemplate.convertAndSend(destination, dto);
        });

    }



}
