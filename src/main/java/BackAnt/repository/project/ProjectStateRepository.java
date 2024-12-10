package BackAnt.repository.project;

import BackAnt.entity.project.ProjectState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
    날짜 : 2024/12/2
    이름 : 강은경
    내용 : ProjectStateRepository 생성
*/
@Repository
public interface ProjectStateRepository extends JpaRepository<ProjectState, Long> {

    // 프로젝트id에 따른 작업상태 조회
    List<ProjectState> findAllByProjectId(Long projectId);




}