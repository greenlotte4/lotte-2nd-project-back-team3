package BackAnt.controller.project;

import BackAnt.dto.project.ProjectDTO;
import BackAnt.dto.project.ProjectTaskAttributeDTO;
import BackAnt.entity.enums.AttributeType;
import BackAnt.repository.UserRepository;
import BackAnt.repository.project.ProjectRepository;
import BackAnt.repository.project.ProjectTaskAttributeRepository;
import BackAnt.service.project.ProjectCollaboratorService;
import BackAnt.service.project.ProjectService;
import BackAnt.service.UserService;
import BackAnt.service.project.ProjectTaskAttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    날 짜 : 2024/12/22(일)
    담당자 : 강은경
    내 용 : Project 를 위한 Controller 생성
*/

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/project/task/attribute")
public class ProjectTaskAttributeController {

    private final ProjectService projectService;
    private final ModelMapper modelMapper;
    private final ProjectTaskAttributeService projectTaskAttributeService;

    // 프로젝트 작업 속성 등록
    @PostMapping("/insert")
    public ResponseEntity<ProjectTaskAttributeDTO> addAttribute(@RequestBody ProjectTaskAttributeDTO projectTaskAttributeDTO) {
        log.info("작업속성으로 들어오는 dto : " + projectTaskAttributeDTO);

        ProjectTaskAttributeDTO savedDto = projectTaskAttributeService.addAttribute(projectTaskAttributeDTO);
        log.info("작업속성 저장 반환되는 dto : " + savedDto);

        return ResponseEntity.ok(savedDto);
    }

    // 프로젝트 작업 속성 타입별 조회
    @GetMapping("/select/{type}")
    public ResponseEntity<List<ProjectTaskAttributeDTO>> getAttributesByType(@PathVariable AttributeType type) {
        log.info("작업속성 type : " + type);

        List<ProjectTaskAttributeDTO> attributes = projectTaskAttributeService.getAttributesByType(type);
        return ResponseEntity.ok(attributes);
    }

    // 프로젝트 작업 속성 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<ProjectTaskAttributeDTO> updateAttribute(@PathVariable Long id, @RequestBody ProjectTaskAttributeDTO projectTaskAttributeDTO) {
        log.info("속성 수정 id : " + id);
        log.info("속성 수정 projectTaskAttributeDTO : " + projectTaskAttributeDTO);

        ProjectTaskAttributeDTO updatedDto = projectTaskAttributeService.updateAttribute(id, projectTaskAttributeDTO);
        return ResponseEntity.ok(updatedDto);
    }

    // 프로젝트 작업 속성 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAttribute(@PathVariable Long id) {
        log.info("속성 삭제 id : " + id);
        
        projectTaskAttributeService.deleteAttribute(id);
        return ResponseEntity.ok("Attribute successfully deleted.");
    }


}