package BackAnt.service;

import BackAnt.dto.project.ProjectTaskDTO;
import BackAnt.entity.User;
import BackAnt.entity.project.ProjectState;
import BackAnt.entity.project.ProjectTask;
import BackAnt.entity.project.ProjectTaskAssignment;
import BackAnt.repository.project.ProjectStateRepository;
import BackAnt.repository.project.ProjectTaskAssignmentRepository;
import BackAnt.repository.project.ProjectTaskRepository;
import BackAnt.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final ProjectTaskAssignmentRepository projectTaskAssignmentRepository;


    // 프로젝트 작업 등록
    @Transactional
    public ProjectTaskDTO createTask(ProjectTaskDTO taskDTO) {

        log.info("taskDTO : " + taskDTO);

        // 1. ProjectTask 생성 및 저장
        ProjectTask task = modelMapper.map(taskDTO, ProjectTask.class);
        // 상태 확인 및 연결
        ProjectState projectState = projectStateRepository.findById(taskDTO.getStateId())
                .orElseThrow(() -> new IllegalArgumentException("State not found with ID: " + taskDTO.getStateId()));
        task.setState(projectState);
        // 작업 저장
        ProjectTask savedTask = projectTaskRepository.save(task);

        // 2. ProjectTaskAssignment 생성 및 저장
        if (taskDTO.getAssignedUserIds() != null && !taskDTO.getAssignedUserIds().isEmpty()) {
            List<ProjectTaskAssignment> assignments = taskDTO.getAssignedUserIds().stream().map(userId -> {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
                return ProjectTaskAssignment.builder()
                        .task(savedTask)
                        .user(user)
                        .build();
            }).collect(Collectors.toList());

            projectTaskAssignmentRepository.saveAll(assignments);
        }

        return modelMapper.map(savedTask, ProjectTaskDTO.class);
    }

    // 특정 상태 id로 작업 조회
    public List<ProjectTaskDTO> getTasksWithAssignedUsers(Long stateId) {
        List<ProjectTask> tasks = projectTaskRepository.findAllByStateId(stateId);

        // 각 작업에 할당된 사용자 ID들을 수집하여 ProjectTaskDTO에 매핑
        return tasks.stream()
                .map(task -> {
                    ProjectTaskDTO taskDTO = modelMapper.map(task, ProjectTaskDTO.class);

                    // 프로젝트 작업에 할당된 사용자 IDs를 가져오기 위해 project_task_assignment을 조회
                    List<Long> assignedUserIds = projectTaskAssignmentRepository.findByTaskId(task.getId())
                            .stream()
                            .map(assignment -> assignment.getUser().getId()) // User ID만 추출
                            .collect(Collectors.toList());

                    taskDTO.setAssignedUserIds(assignedUserIds); // 해당 작업에 할당된 사용자 ID 목록 설정
                    return taskDTO;
                })
                .collect(Collectors.toList());
    }


    // 프로젝트 작업 수정
    @Transactional
    public ProjectTaskDTO updateTask(Long taskId, ProjectTaskDTO projectTaskDTO ) {

        // 수정할 작업 가져오기
        ProjectTask existingTask = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        log.info("existingTask : " + existingTask);


        // 프로젝트 작업상태 확인
        ProjectState projectState = projectStateRepository.findById(projectTaskDTO.getStateId())
                .orElseThrow(() -> new IllegalArgumentException("State not found with ID: " + projectTaskDTO.getStateId()));
        log.info("projectState : " + projectState);

        // 기존 작업의 필드 업데이트
        existingTask.setTitle(projectTaskDTO.getTitle());
        existingTask.setContent(projectTaskDTO.getContent());
        existingTask.setPriority(projectTaskDTO.getPriority());
        existingTask.setStatus(projectTaskDTO.getStatus());
        existingTask.setSize(projectTaskDTO.getSize());
        existingTask.setDueDate(projectTaskDTO.getDueDate());
        existingTask.setPosition(projectTaskDTO.getPosition());
        existingTask.setState(projectState);

        // 변경 내용 저장
        ProjectTask updatedTask = projectTaskRepository.save(existingTask);
        log.info("updatedTask : " + updatedTask);

        return modelMapper.map(updatedTask, ProjectTaskDTO.class);

    }

    // 작업 드래그앤드랍시 프로젝트 작업 상태 수정
    @Transactional
    public ProjectTaskDTO updateTaskState(Long taskId, Long newStateId, int newPosition) {

        // 기존 작업 조회
        ProjectTask projectTask = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        log.info("projectTask : " + projectTask);

        // 새로운 작업 상태 조회
        ProjectState newState = projectStateRepository.findById(newStateId)
                .orElseThrow(() -> new IllegalArgumentException("State not found with ID: " + newStateId));
        log.info("newState : " + newState);


        // 기존 작업의 작업상태와 위치를 업데이트
        projectTask.setState(newState);
        projectTask.setPosition(newPosition);

        // 변경된 작업 저장
        ProjectTask updatedTask = projectTaskRepository.save(projectTask);
        log.info("updatedTask : " + updatedTask);

        return modelMapper.map(updatedTask, ProjectTaskDTO.class);


    }




}
