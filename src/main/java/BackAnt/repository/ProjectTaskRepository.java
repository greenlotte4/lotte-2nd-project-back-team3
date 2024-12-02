package BackAnt.repository;

import BackAnt.entity.ProjectState;
import BackAnt.entity.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
    날짜 : 2024/12/2
    이름 : 강은경
    내용 : ProjectTaskRepository 생성
*/
@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {


}