package BackAnt.service;

import BackAnt.repository.ProjectRepository;
import BackAnt.repository.ProjectStateRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/*
    날 짜 : 2024/12/2(월)
    담당자 : 강은경
    내 용 : ProjectState 를 위한 Service 생성
*/

@RequiredArgsConstructor
@Service
public class ProjectStateService {
    private final ProjectStateRepository projectStateRepository;
    private final ModelMapper modelMapper;


}
