package BackAnt.controller.project;

import BackAnt.document.page.PageDocument;
import BackAnt.dto.PageDTO;
import BackAnt.dto.ProjectDTO;
import BackAnt.service.PageImageService;
import BackAnt.service.PageService;
import BackAnt.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/*
    날 짜 : 2024/12/2(월)
    담당자 : 강은경
    내 용 : Project 를 위한 Controller 생성
*/

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;
    private final ModelMapper modelMapper;

    // 프로젝트 저장
    @PostMapping("/add")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO, String uid) {

        // 현재 로그인한 사용자 ID 가져오기
        //String uid = userDetails.getUsername();
        uid = "ekkang";

        ProjectDTO savedProjectDTO = projectService.createProject(projectDTO, uid);
        log.info("savedProjectDTO " + savedProjectDTO);

        return ResponseEntity.ok(savedProjectDTO);
    }

    // 프로젝트 조회
    /*@GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }*/




}