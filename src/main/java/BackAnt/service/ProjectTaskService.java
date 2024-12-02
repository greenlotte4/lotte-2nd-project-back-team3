package BackAnt.service;

import BackAnt.repository.ProjectStateRepository;
import BackAnt.repository.ProjectTaskRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/*
    날 짜 : 2024/12/2(월)
    담당자 : 강은경
    내 용 : ProjectTask 를 위한 Service 생성
*/

@RequiredArgsConstructor
@Service
public class ProjectTaskService {
    private final ProjectTaskRepository projectTaskRepository;
    private final ModelMapper modelMapper;


}
