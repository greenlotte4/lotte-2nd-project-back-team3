package BackAnt.service;

import BackAnt.repository.ProjectCollaboratorRepository;
import BackAnt.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/*
    날 짜 : 2024/12/2(월)
    담당자 : 강은경
    내 용 : ProjectCollaborator 를 위한 Service 생성
*/

@RequiredArgsConstructor
@Service
public class ProjectCollaboratorService {
    private final ProjectCollaboratorRepository projectCollaboratorRepository;
    private final ModelMapper modelMapper;


}
