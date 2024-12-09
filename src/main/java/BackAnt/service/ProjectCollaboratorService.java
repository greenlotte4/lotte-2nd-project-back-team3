package BackAnt.service;

import BackAnt.dto.UserDTO;
import BackAnt.entity.User;
import BackAnt.entity.project.Project;
import BackAnt.entity.project.ProjectCollaborator;
import BackAnt.repository.ProjectCollaboratorRepository;
import BackAnt.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
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


    // 프로젝트별 협업자 추가
    public void addCollaborator(Project project, User user, int type) {

        // 협업자 저장
       ProjectCollaborator projectCollaborator = ProjectCollaborator.builder()
               .project(project)
               .user(user)
               .isOwner(false) // 새로 추가되는 협업자는 isOwner false
               .type(type)
               .build();

       projectCollaboratorRepository.save(projectCollaborator);
    }

    // 프로젝트별 협업자 조회
    public List<UserDTO> getUsersByProjectId(Long projectId) {

        log.info("projectId : " + projectId);
        List<User> users = projectCollaboratorRepository.findUsersByProjectId(projectId);
        log.info("users : " + users);

        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }


    // 프로젝트 id와 사용자 id를 기준으로 협업자 삭제
    public void deleteCollaborator(Long projectId, Long userId) {
        ProjectCollaborator collaborator = projectCollaboratorRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 협업자가 존재하지 않습니다."));

        projectCollaboratorRepository.delete(collaborator);
    }

}
