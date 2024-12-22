package BackAnt.repository.project;

import BackAnt.entity.project.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT t FROM ProjectTask t " +
            "LEFT JOIN FETCH t.priority " +
            "LEFT JOIN FETCH t.size " +
            "WHERE t.state.id = :stateId")
    List<ProjectTask> findAllByStateIdWithAttributes(@Param("stateId") Long stateId);

}