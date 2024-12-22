package BackAnt.service.project;

import BackAnt.dto.project.ProjectAssignedUserDTO;
import BackAnt.dto.project.ProjectTaskDTO;
import BackAnt.entity.User;
import BackAnt.entity.project.*;
import BackAnt.repository.project.*;
import BackAnt.repository.UserRepository;
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
    private final ProjectCollaboratorRepository projectCollaboratorRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ProjectTaskAttributeRepository projectTaskAttributeRepository;


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

        // 우선순위 연결
        if (taskDTO.getPriorityId() != null) {
            ProjectTaskAttribute priority = projectTaskAttributeRepository.findById(taskDTO.getPriorityId())
                    .orElseThrow(() -> new IllegalArgumentException("Priority not found with ID: " + taskDTO.getPriorityId()));
            task.setPriority(priority);
        }

        // 크기 연결
        if (taskDTO.getSizeId() != null) {
            ProjectTaskAttribute size = projectTaskAttributeRepository.findById(taskDTO.getSizeId())
                    .orElseThrow(() -> new IllegalArgumentException("Size not found with ID: " + taskDTO.getSizeId()));
            task.setSize(size);
        }

        // 작업 저장
        ProjectTask savedTask = projectTaskRepository.save(task);

        // 2. ProjectTaskAssignment 생성 및 저장
        if (taskDTO.getAssignedUser() != null && !taskDTO.getAssignedUser().isEmpty()) {
            List<ProjectTaskAssignment> assignments = taskDTO.getAssignedUser().stream().map(userId -> {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
                return ProjectTaskAssignment.builder()
                        .task(savedTask)
                        .user(user)
                        .build();
            }).collect(Collectors.toList());

            projectTaskAssignmentRepository.saveAll(assignments);
        }

        // 3. 할당된 사용자 정보를 taskDTO에 설정
        ProjectTaskDTO responseDTO = modelMapper.map(savedTask, ProjectTaskDTO.class);

        // 우선순위 및 크기 정보 설정
        if (savedTask.getPriority() != null) {
            responseDTO.setPriorityId(savedTask.getPriority().getId());
            responseDTO.setPriorityName(savedTask.getPriority().getName());
        }

        if (savedTask.getSize() != null) {
            responseDTO.setSizeId(savedTask.getSize().getId());
            responseDTO.setSizeName(savedTask.getSize().getName());
        }

        // ProjectTaskAssignment에서 할당된 사용자들의 ID를 가져와서 할당
        List<Long> assignedUserIds = projectTaskAssignmentRepository.findByTaskId(savedTask.getId()).stream()
                .map(assignment -> assignment.getUser().getId())
                .collect(Collectors.toList());

        responseDTO.setAssignedUser(assignedUserIds); // 작업의 assignedUser 필드에 ID 목록을 설정

        // 4. 각 할당된 사용자 ID에 대해 User 정보를 가져와 ProjectAssignedUserDTO로 변환
        List<ProjectAssignedUserDTO> assignedUsers = assignedUserIds.stream()
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

                    return ProjectAssignedUserDTO.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .position(user.getPosition())
                            .profileImageUrl(user.getProfileImageUrl())
                            .build();
                })
                .collect(Collectors.toList());

        responseDTO.setAssignedUserDetails(assignedUsers); // 상세 사용자 정보 설정

        responseDTO.setAction("taskInsert");
        responseDTO.setProjectId(projectState.getProject().getId());
        responseDTO.setStateId(projectState.getId());
        log.info("작업 추가 responseDTO : " + responseDTO);
        log.info("해당 작업상태에 해당하는 projectId : " + projectState.getProject().getId());

        // 웹소켓을 쏴주기 위한 프로젝트 id에 다른 협업자 조회
        List<ProjectCollaborator> projectCollaborators = projectCollaboratorRepository.findByProject_Id(projectState.getProject().getId());
        log.info("projectCollaborators : " + projectCollaborators);

        // 2. WebSocket을 통한 실시간 알림 전송
        projectCollaborators.forEach(projectCollaborator -> {
            String destination = "/topic/project/" + projectCollaborator.getUser().getId();
            log.info("경로" + destination);
            messagingTemplate.convertAndSend(destination, responseDTO);
        });

        return responseDTO;
    }


    // 특정 상태 id로 작업 조회
    public List<ProjectTaskDTO> getTasksWithAssignedUsers(Long stateId) {
        // Fetch Join을 사용하여 관련 데이터 한 번에 가져오기
        List<ProjectTask> tasks = projectTaskRepository.findAllByStateIdWithAttributes(stateId);
        log.info("작업 조회 tasks : " + tasks);

        // 각 작업에 대한 DTO 변환 및 매핑
        return tasks.stream()
                .map(task -> {
                    ProjectTaskDTO taskDTO = modelMapper.map(task, ProjectTaskDTO.class);

                    // 우선순위 정보 설정
                    if (task.getPriority() != null) {
                        taskDTO.setPriorityId(task.getPriority().getId());
                        taskDTO.setPriorityName(task.getPriority().getName());
                    }

                    // 크기 정보 설정
                    if (task.getSize() != null) {
                        taskDTO.setSizeId(task.getSize().getId());
                        taskDTO.setSizeName(task.getSize().getName());
                    }

                    // 할당된 사용자 ID 조회
                    List<Long> assignedUserIds = projectTaskAssignmentRepository.findByTaskId(task.getId())
                            .stream()
                            .map(assignment -> assignment.getUser().getId())
                            .collect(Collectors.toList());

                    taskDTO.setAssignedUser(assignedUserIds);

                    // 할당된 사용자 상세 정보 변환 및 설정
                    List<ProjectAssignedUserDTO> assignedUsers = assignedUserIds.stream()
                            .map(userId -> {
                                User user = userRepository.findById(userId)
                                        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

                                return ProjectAssignedUserDTO.builder()
                                        .id(user.getId())
                                        .name(user.getName())
                                        .position(user.getPosition())
                                        .profileImageUrl(user.getProfileImageUrl())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    taskDTO.setAssignedUserDetails(assignedUsers);

                    return taskDTO;
                })
                .collect(Collectors.toList());
    }


    // 프로젝트 작업 수정
    @Transactional
    public ProjectTaskDTO updateTask(Long taskId, ProjectTaskDTO projectTaskDTO) {
        log.info("taskId: " + taskId);
        log.info("projectTaskDTO: " + projectTaskDTO);

        // 1. 수정할 작업 가져오기
        ProjectTask existingTask = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        log.info("existingTask: " + existingTask);

        // 2. 작업 상태 확인 및 연결
        ProjectState projectState = projectStateRepository.findById(projectTaskDTO.getStateId())
                .orElseThrow(() -> new IllegalArgumentException("State not found with ID: " + projectTaskDTO.getStateId()));
        existingTask.setState(projectState);

        // 3. 기존 작업 필드 업데이트
        existingTask.setTitle(projectTaskDTO.getTitle());
        existingTask.setContent(projectTaskDTO.getContent());
        existingTask.setStatus(projectTaskDTO.getStatus());
        existingTask.setDueDate(projectTaskDTO.getDueDate());
        existingTask.setPosition(projectTaskDTO.getPosition());

        // 우선순위 연결
        if (projectTaskDTO.getPriorityId() != null) {
            ProjectTaskAttribute priority = projectTaskAttributeRepository.findById(projectTaskDTO.getPriorityId())
                    .orElseThrow(() -> new IllegalArgumentException("Priority not found with ID: " + projectTaskDTO.getPriorityId()));
            existingTask.setPriority(priority);
        } else {
            existingTask.setPriority(null);
        }

        // 크기 연결
        if (projectTaskDTO.getSizeId() != null) {
            ProjectTaskAttribute size = projectTaskAttributeRepository.findById(projectTaskDTO.getSizeId())
                    .orElseThrow(() -> new IllegalArgumentException("Size not found with ID: " + projectTaskDTO.getSizeId()));
            existingTask.setSize(size);
        } else {
            existingTask.setSize(null);
        }

        // 작업의 담당자 수정
        if (projectTaskDTO.getAssignedUser() != null) {
            // 기존 담당자 목록 가져오기
            List<Long> currentAssignedUserIds = projectTaskAssignmentRepository.findByTaskId(taskId).stream()
                    .map(assignment -> assignment.getUser().getId())
                    .collect(Collectors.toList());

            // 새로 할당할 사용자 목록 가져오기
            List<Long> newAssignedUserIds = projectTaskDTO.getAssignedUser();

            // 기존 담당자 중 삭제된 사용자 처리
            List<Long> usersToRemove = currentAssignedUserIds.stream()
                    .filter(id -> !newAssignedUserIds.contains(id)) // 새 목록에 없는 사용자는 삭제
                    .collect(Collectors.toList());

            // 삭제할 담당자 삭제
            projectTaskAssignmentRepository.deleteByTaskIdAndUserIdIn(taskId, usersToRemove);

            // 추가할 사용자 처리 (새로 추가된 사용자만 추가)
            List<Long> usersToAdd = newAssignedUserIds.stream()
                    .filter(id -> !currentAssignedUserIds.contains(id)) // 새 목록에 있는데 기존 목록에는 없는 사용자는 추가
                    .collect(Collectors.toList());

            if (!usersToAdd.isEmpty()) {
                List<ProjectTaskAssignment> assignments = usersToAdd.stream().map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
                    return ProjectTaskAssignment.builder()
                            .task(existingTask)
                            .user(user)
                            .build();
                }).collect(Collectors.toList());

                projectTaskAssignmentRepository.saveAll(assignments);  // 새 사용자 할당
            }
        }

        // 수정된 작업 저장
        ProjectTask updatedTask = projectTaskRepository.save(existingTask);

        // 수정된 작업 DTO 반환
        ProjectTaskDTO responseDTO = modelMapper.map(updatedTask, ProjectTaskDTO.class);

        // 우선순위 설정
        if (updatedTask.getPriority() != null) {
            responseDTO.setPriorityId(updatedTask.getPriority().getId());
            responseDTO.setPriorityName(updatedTask.getPriority().getName());
        }

        // 크기 설정
        if (updatedTask.getSize() != null) {
            responseDTO.setSizeId(updatedTask.getSize().getId());
            responseDTO.setSizeName(updatedTask.getSize().getName());
        }

        // 할당된 사용자 정보 가져오기
        List<Long> assignedUserIds = projectTaskAssignmentRepository.findByTaskId(updatedTask.getId()).stream()
                .map(assignment -> assignment.getUser().getId())
                .collect(Collectors.toList());

        responseDTO.setAssignedUser(assignedUserIds);

        // 각 할당된 사용자 ID에 대해 User 정보를 가져와 ProjectAssignedUserDTO로 변환
        List<ProjectAssignedUserDTO> assignedUsers = assignedUserIds.stream()
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

                    return ProjectAssignedUserDTO.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .position(user.getPosition())
                            .profileImageUrl(user.getProfileImageUrl())
                            .build();
                })
                .collect(Collectors.toList());

        responseDTO.setAssignedUserDetails(assignedUsers); // 상세 사용자 정보 설정

        responseDTO.setProjectId(projectState.getProject().getId());
        responseDTO.setStateId(projectTaskDTO.getStateId());
        responseDTO.setAction("taskUpdate");
        log.info("작업수정할때 responseDTO : " + responseDTO);

        // 웹소켓을 쏴주기 위한 프로젝트 id에 다른 협업자 조회
        List<ProjectCollaborator> projectCollaborators = projectCollaboratorRepository.findByProject_Id(projectState.getProject().getId());
        log.info("projectCollaborators : " + projectCollaborators);

        // 2. WebSocket을 통한 실시간 알림 전송
        projectCollaborators.forEach(projectCollaborator -> {
            String destination = "/topic/project/" + projectCollaborator.getUser().getId();
            log.info("경로" + destination);
            messagingTemplate.convertAndSend(destination, responseDTO);
        });

        return responseDTO;
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

        ProjectTaskDTO dto = modelMapper.map(updatedTask, ProjectTaskDTO.class);
        dto.setProjectId(projectTask.getState().getProject().getId());
        dto.setStateId(newStateId);
        dto.setAction("taskDrag");
        dto.getAssignedUserDetails();
        dto.setSizeId(projectTask.getSize().getId());
        dto.setSizeName(projectTask.getSize().getName());
        dto.setPriorityId(projectTask.getPriority().getId());
        dto.setPriorityName(projectTask.getPriority().getName());
        log.info("작업 드래그앤드랍 dto : " + dto);

        // 웹소켓을 쏴주기 위한 프로젝트 id에 다른 협업자 조회
        List<ProjectCollaborator> projectCollaborators = projectCollaboratorRepository.findByProject_Id(projectTask.getState().getProject().getId());
        log.info("projectCollaborators : " + projectCollaborators);

        // 2. WebSocket을 통한 실시간 알림 전송
        projectCollaborators.forEach(projectCollaborator -> {
            String destination = "/topic/project/" + projectCollaborator.getUser().getId();
            log.info("경로" + destination);
            messagingTemplate.convertAndSend(destination, dto);
        });

        return dto;
    }

    // 프로젝트 작업 삭제
    @Transactional
    public void deleteTaskById(Long taskId) {

        // 로그 추가
        System.out.println("Deleting task with ID: " + taskId);

        // 해당 작업 조회
        ProjectTask projectTask = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        // 작업에 할당된 담당자 삭제
        projectTaskAssignmentRepository.deleteByTaskId(taskId);

        // 작업 삭제
        projectTaskRepository.deleteById(taskId);

        ProjectTaskDTO dto = modelMapper.map(projectTask, ProjectTaskDTO.class);
        dto.setAction("taskDelete");
        dto.setProjectId(projectTask.getState().getProject().getId());
        log.info("작업 삭제 dto : " + dto);

        // 웹소켓을 쏴주기 위한 프로젝트 id에 다른 협업자 조회
        List<ProjectCollaborator> projectCollaborators = projectCollaboratorRepository.findByProject_Id(projectTask.getState().getProject().getId());
        log.info("projectCollaborators : " + projectCollaborators);

        // 2. WebSocket을 통한 실시간 알림 전송
        projectCollaborators.forEach(projectCollaborator -> {
            String destination = "/topic/project/" + projectCollaborator.getUser().getId();
            log.info("경로" + destination);
            messagingTemplate.convertAndSend(destination, dto);
        });
    }


}
