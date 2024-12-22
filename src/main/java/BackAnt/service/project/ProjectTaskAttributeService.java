package BackAnt.service.project;

import BackAnt.dto.project.ProjectAssignedUserDTO;
import BackAnt.dto.project.ProjectTaskAttributeDTO;
import BackAnt.dto.project.ProjectTaskDTO;
import BackAnt.entity.User;
import BackAnt.entity.enums.AttributeType;
import BackAnt.entity.project.*;
import BackAnt.repository.UserRepository;
import BackAnt.repository.project.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
    날 짜 : 2024/12/22(일)
    담당자 : 강은경
    내 용 : ProjectTaskAttribute 를 위한 Service 생성
*/

@Log4j2
@RequiredArgsConstructor
@Service
public class ProjectTaskAttributeService {

    private final ModelMapper modelMapper;
    private final ProjectTaskAttributeRepository projectTaskAttributeRepository;

    // 속성 등록
    public ProjectTaskAttributeDTO addAttribute(ProjectTaskAttributeDTO projectTaskAttributeDTO) {

        ProjectTaskAttribute projectTaskAttribute = modelMapper.map(projectTaskAttributeDTO, ProjectTaskAttribute.class);

        // 저장
        ProjectTaskAttribute savedProjectTaskAttribute = projectTaskAttributeRepository.save(projectTaskAttribute);

        return modelMapper.map(savedProjectTaskAttribute, ProjectTaskAttributeDTO.class);
    }

    // 프로젝트 작업 속성 조회
    public List<ProjectTaskAttributeDTO> getAttributesByType(AttributeType type) {

        List<ProjectTaskAttribute> attributes = projectTaskAttributeRepository.findByType(type);
        return attributes.stream()
                .map(attribute -> modelMapper.map(attribute, ProjectTaskAttributeDTO.class))
                .collect(Collectors.toList());

    }

    // 프로젝트 작업 속성 수정
    public ProjectTaskAttributeDTO updateAttribute(Long id, ProjectTaskAttributeDTO projectTaskAttributeDTO) {
       ProjectTaskAttribute projectTaskAttribute = projectTaskAttributeRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Attribute not found"));

        // 수정한 속성 저장
        projectTaskAttribute.setName(projectTaskAttributeDTO.getName());
        ProjectTaskAttribute updatedProjectTaskAttribute = projectTaskAttributeRepository.save(projectTaskAttribute);

        return modelMapper.map(updatedProjectTaskAttribute, ProjectTaskAttributeDTO.class);
    }

    // 프로젝트 작업 속성 삭제
    public void deleteAttribute(Long id) {
        if(!projectTaskAttributeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attribute not found");
        }
        projectTaskAttributeRepository.deleteById(id);
    }

}
