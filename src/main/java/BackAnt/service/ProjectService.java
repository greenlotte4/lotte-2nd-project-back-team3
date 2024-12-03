package BackAnt.service;

import BackAnt.document.page.PageDocument;
import BackAnt.dto.PageRequestDTO;
import BackAnt.dto.ProjectDTO;
import BackAnt.entity.Project;
import BackAnt.entity.ProjectCollaborator;
import BackAnt.entity.User;
import BackAnt.repository.ProjectCollaboratorRepository;
import BackAnt.repository.ProjectRepository;
import BackAnt.repository.UserRepository;
import BackAnt.repository.mongoDB.PageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
    날 짜 : 2024/12/2(월)
    담당자 : 강은경
    내 용 : Project 를 위한 Service 생성
*/

@RequiredArgsConstructor
@Service
@Log4j2
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectCollaboratorRepository projectCollaboratorRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // 프로젝트 생성
    public ProjectDTO createProject(ProjectDTO projectDTO, String uid) {

        // 1. 로그인한 사용자 조회
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2. 프로젝트 저장
        Project project = Project.builder()
                .projectName(projectDTO.getProjectName())
                .status(0) // 진행중
                .build();

        Project savedProject = projectRepository.save(project);
        log.info("savedProject: " + savedProject);

        // 3. ProjectCollaborator 생성 및 저장
        ProjectCollaborator collaborator = ProjectCollaborator.builder()
                .project(savedProject)
                .user(user)
                .isOwner(true) // 생성자임을 표시
                .type(1)    // 최고관리자
                .build();
        log.info("collaborator: " + collaborator);

        projectCollaboratorRepository.save(collaborator);

        // 4. 저장된 데이터를 DTO로 반환
        return ProjectDTO.builder()
                .id(savedProject.getId())
                .projectName(savedProject.getProjectName())
                .status(savedProject.getStatus())
                .build();

    }

    // 프로젝트 생성
   /* public ProjectDTO createProject(ProjectDTO projectDTO) {

        // DTO > entity 변환
        Project project = modelMapper.map(projectDTO, Project.class);

        // 기본값 설정
        project.setStatus(0); // 기본값으로 일단 진행중

        // 저장
        Project savedProject = projectRepository.save(project);

        // entity > DTO 변환
        return modelMapper.map(savedProject, ProjectDTO.class);

    }*/


    // 모든 프로젝트 데이터를 반환
   /* public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        // Entity -> DTO 변환
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }
*/




}
