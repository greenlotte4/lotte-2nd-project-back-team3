package BackAnt.service.project;

import BackAnt.dto.NotificationDTO;
import BackAnt.dto.user.UserDTO;
import BackAnt.dto.project.ProjectCollaboratorDTO;
import BackAnt.dto.project.UserForProjectDTO;
import BackAnt.entity.User;
import BackAnt.entity.project.Project;
import BackAnt.entity.project.ProjectCollaborator;
import BackAnt.repository.UserRepository;
import BackAnt.repository.project.ProjectCollaboratorRepository;
import BackAnt.repository.project.ProjectTaskAssignmentRepository;
import BackAnt.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/*
    날 짜 : 2024/12/2(월)
    담당자 : 강은경
    내 용 : ProjectCollaborator 를 위한 Service 생성
*/

@Log4j2
@RequiredArgsConstructor
@Service
public class ProjectCollaboratorService {
    private final ProjectCollaboratorRepository projectCollaboratorRepository;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final ProjectTaskAssignmentRepository projectTaskAssignmentRepository;


    // 프로젝트별 협업자 추가
    public void addCollaborator(Project project, User user, int type, long id) {

        // 협업자 저장
       ProjectCollaborator projectCollaborator = ProjectCollaborator.builder()
               .project(project)
               .user(user)
               .isOwner(false) // 새로 추가되는 협업자는 isOwner false
               .type(type)
               .build();

        projectCollaboratorRepository.save(projectCollaborator);

        Optional<ProjectCollaborator> ownerUser = projectCollaboratorRepository.findByProjectIdAndUserId(project.getId(), id);
        if(ownerUser.isPresent()) {
            // WebSocket을 통한 실시간 알림 전송
            NotificationDTO notification = NotificationDTO.builder()
                    .targetType("USER")
                    .targetId(user.getId()) // Approver ID
                    .senderId(ownerUser.get().getId())
                    .message(ownerUser.get().getUser().getName()    + "님이 당신을 " + project.getProjectName() + "에 초대하였습니다.")
                    .metadata(Map.of(
                            "url", "/antwork/project/view?id="+ project.getId(),
                            "type", "프로젝트 초대",
                            "title", project.getProjectName()
                    ))
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationService.createAndSendNotification(notification);
        }

        // 웹소켓 쏴주기 위한 프로젝트 id에 따른 협업자 조회
        List<ProjectCollaborator> projectCollaborators = projectCollaboratorRepository.findByProject_Id(project.getId());
        log.info("프로젝트협업자 초대 projectCollaborators : " + projectCollaborators);

        ProjectCollaboratorDTO dto = new ProjectCollaboratorDTO();
        dto.setProjectId(project.getId());
        dto.setUserId(user.getId());
        dto.setUsername(user.getName());
        dto.setAction("collaboratorAdd");

        log.info("프로젝트 협업자 초대 dto : " + dto);

        // 2. WebSocket을 통한 실시간 알림 전송
        projectCollaborators.forEach(collaborator -> {
            String destination = "/topic/project/" + collaborator.getUser().getId();
            log.info("경로" + destination);
            messagingTemplate.convertAndSend(destination, dto);
        });

    }

    // 프로젝트별 협업자 조회
    public List<UserForProjectDTO> getUsersByProjectId(Long projectId) {
        log.info("projectId : " + projectId);

        // 프로젝트에 속한 협업자 목록 조회
        List<ProjectCollaborator> collaborators = projectCollaboratorRepository.findByProject_Id(projectId);
        log.info("collaborators : " + collaborators);

        // UserForProjectDTO 생성
        return collaborators.stream()
                .map(collaborator -> {
                    // 기존 UserDTO로 변환
                    UserDTO userDTO = modelMapper.map(collaborator.getUser(), UserDTO.class);

                    // UserForProjectDTO로 확장하고 isOwner 필드 설정
                    UserForProjectDTO userForProjectDTO = modelMapper.map(userDTO, UserForProjectDTO.class);
                    userForProjectDTO.setOwner(collaborator.isOwner());

                    return userForProjectDTO;
                })
                .collect(Collectors.toList());
    }



    // 프로젝트 id와 사용자 id를 기준으로 협업자 삭제
    public void deleteCollaborator(Long projectId, Long userId) {

        // 협업자 조회 
        ProjectCollaborator collaborator = projectCollaboratorRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 협업자가 존재하지 않습니다."));

        // 협업자 삭제
        projectCollaboratorRepository.delete(collaborator);

        // 해당 협업자가 할당된 모든 작업 삭제
        projectTaskAssignmentRepository.deleteByProjectIdAndUserId(projectId, userId);

        // 웹소켓 쏴주기 위한 프로젝트 id에 따른 협업자 조회
        List<ProjectCollaborator> projectCollaborators = projectCollaboratorRepository.findByProject_Id(projectId);
        log.info("1111111111projectCollaborators : " + projectCollaborators);

        ProjectCollaboratorDTO dto = new ProjectCollaboratorDTO();
        dto.setProjectId((collaborator.getProject().getId()));
        dto.setUserId(collaborator.getUser().getId());
        dto.setUsername(collaborator.getUser().getName());
        dto.setAction("collaboratorDelete");

        log.info("dto : " + dto);

        // 2. WebSocket을 통한 실시간 알림 전송(삭제 대상 제외)
        projectCollaborators.stream()
                .filter(projectCollaborator -> !projectCollaborator.getUser().getId().equals(userId)) // 삭제된 사용자 제외
                .forEach(projectCollaborator -> {
                    String destination = "/topic/project/" + projectCollaborator.getUser().getId();
                    log.info("경로 : " + destination);
                    messagingTemplate.convertAndSend(destination, dto);
                });

    }

    // 프로젝트에 협업자가 몇 명 등록되어 있는지 세는 메서드
    public int countCollaboratorsByProjectId(Long projectId) {
        return projectCollaboratorRepository.countByProject_Id(projectId);
    }

}
