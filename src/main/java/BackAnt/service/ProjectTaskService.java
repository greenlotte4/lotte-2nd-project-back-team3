package BackAnt.service;

import BackAnt.dto.ProjectTaskDTO;
import BackAnt.entity.ProjectState;
import BackAnt.entity.ProjectTask;
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
    내 용 : ProjectTask 를 위한 Service 생성
*/

@Log4j2
@RequiredArgsConstructor
@Service
public class ProjectTaskService {

    private final ProjectTaskRepository projectTaskRepository;
    private final ModelMapper modelMapper;
    private final ProjectStateRepository projectStateRepository;


    // 프로젝트 작업 등록
    @Transactional
    public ProjectTaskDTO createTask(ProjectTaskDTO taskDTO) {
        ProjectTask task = modelMapper.map(taskDTO, ProjectTask.class);

        // 상태 확인 및 연결
        ProjectState projectState = projectStateRepository.findById(taskDTO.getStateId())
                .orElseThrow(() -> new IllegalArgumentException("State not found with ID: " + taskDTO.getStateId()));
        task.setState(projectState);

        // 작업 저장
        //task.setPosition(projectState.getTasks().size()); // position 기본값 설정
        ProjectTask savedTask = projectTaskRepository.save(task);

        return modelMapper.map(savedTask, ProjectTaskDTO.class);
    }

    // 특정 상태 id로 작업 조회
    public List<ProjectTaskDTO> getTasksByStateId(Long stateId) {

        List<ProjectTask> tasks = projectTaskRepository.findAllByStateId(stateId);
        log.info("tasks : " + tasks);

        return tasks.stream()
                .map(task -> modelMapper.map(task, ProjectTaskDTO.class))
                .collect(Collectors.toList());
    }


}
