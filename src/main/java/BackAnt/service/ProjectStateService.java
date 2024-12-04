package BackAnt.service;

import BackAnt.dto.ProjectStateDTO;
import BackAnt.entity.Project;
import BackAnt.entity.ProjectState;
import BackAnt.repository.ProjectRepository;
import BackAnt.repository.ProjectStateRepository;
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



}
