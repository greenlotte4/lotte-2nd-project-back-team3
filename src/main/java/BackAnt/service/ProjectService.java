package BackAnt.service;

import BackAnt.dto.project.ProjectDTO;
import BackAnt.entity.project.Project;
import BackAnt.entity.project.ProjectCollaborator;
import BackAnt.entity.User;
import BackAnt.entity.project.ProjectState;
import BackAnt.entity.project.ProjectTask;
import BackAnt.repository.project.*;
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
    private final ProjectStateRepository projectStateRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectTaskAssignmentRepository projectTaskAssignmentRepository;

    // 프로젝트 생성
    public ProjectDTO createProject(ProjectDTO projectDTO, String uid) {

        // 1. 로그인한 사용자 조회
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        log.info("user : " + user);


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

    // 내 프로젝트 조회
    public List<ProjectDTO> getMyProjects(String uid) {
        List<Project> projects = projectCollaboratorRepository.findProjectsByUserUid(uid);

        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    // 프로젝트 상세 페이지
    public ProjectDTO getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        log.info("project: " + project);

        return modelMapper.map(project, ProjectDTO.class);
    }

    // 프로젝트 수정
    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        log.info("project: " + project);

        project.setProjectName(projectDTO.getProjectName());
        project.setStatus(projectDTO.getStatus());

        Project updatedProject = projectRepository.save(project);
        return modelMapper.map(updatedProject, ProjectDTO.class);

    }

    // 프로젝트 삭제
    @Transactional
    public void deleteProject(Long projectId) {

        // 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        log.info("project: " + project);

        // 프로젝트에 속한 상태들 조회
        List<ProjectState> projectStates = projectStateRepository.findAllByProjectId(projectId);

        for(ProjectState projectState : projectStates) {

            // 상태에 속한 모든 작업 조회
            List<ProjectTask> tasks = projectTaskRepository.findAllByStateId(projectState.getId());

            for(ProjectTask projectTask : tasks) {
                // 작업에 할당된 담당자 삭제
                projectTaskAssignmentRepository.deleteByTaskId(projectTask.getId());
            }

            // 작업 삭제
            projectTaskRepository.deleteAll(tasks);

            // 작업상태 삭제
            projectStateRepository.deleteById(projectState.getId());

        }

        // 프로젝트 협업자 삭제
        projectCollaboratorRepository.deleteByProjectId(projectId);

        // 프로젝트 삭제
        projectRepository.deleteById(projectId);

    }




}
