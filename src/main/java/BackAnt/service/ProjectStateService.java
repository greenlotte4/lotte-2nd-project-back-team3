package BackAnt.service;

import BackAnt.dto.project.ProjectStateDTO;
import BackAnt.entity.project.Project;
import BackAnt.entity.project.ProjectState;
import BackAnt.entity.project.ProjectTask;
import BackAnt.repository.ProjectRepository;
import BackAnt.repository.ProjectStateRepository;
import BackAnt.repository.ProjectTaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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


    // 프로젝트 상태 등록
    public ProjectStateDTO addState(ProjectStateDTO projectStateDTO) {
        // 프로젝트 조회
        Project project = projectRepository.findById(projectStateDTO.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // dto -> 엔티티 변환
        ProjectState projectState = modelMapper.map(projectStateDTO, ProjectState.class);
        projectState.setProject(project); // 프로젝트 설정

        // 엔티티 저장
        ProjectState savedState = projectStateRepository.save(projectState);

        return modelMapper.map(savedState, ProjectStateDTO.class);

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

        return modelMapper.map(updatedState, ProjectStateDTO.class);


    }
    
    // 프로젝트 작업 상태 삭제
    @Transactional
    public void deleteState(Long stateId) {

        // 1. 해당 작업상태에 속한 모든 작업 삭제
        List<ProjectTask> tasks = projectTaskRepository.findAllByStateId(stateId);
        projectTaskRepository.deleteAll(tasks);

        // 2. 작업 상태 삭제
        projectStateRepository.deleteById(stateId);

    }


    

}
