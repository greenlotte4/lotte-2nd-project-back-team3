package BackAnt.repository;

import BackAnt.entity.ProjectState;
import BackAnt.entity.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
    날짜 : 2024/12/2
    이름 : 강은경
    내용 : ProjectTaskRepository 생성
*/
@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    // 특정 상태 id로 작업 조회
    List<ProjectTask> findAllByStateId(Long stateId);

}