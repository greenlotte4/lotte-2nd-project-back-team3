package BackAnt.repository.project;

import BackAnt.entity.User;
import BackAnt.entity.project.Project;
import BackAnt.entity.project.ProjectCollaborator;
import BackAnt.entity.project.ProjectTaskAssignment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
    날짜 : 2024/12/10
    이름 : 강은경
    내용 : ProjectTaskAssignmentRepository 생성
*/
@Repository
public interface ProjectTaskAssignmentRepository extends JpaRepository<ProjectTaskAssignment, Long> {

    // 특정 task_id에 대해 할당된 모든 사용자들을 반환하는 메서드
    List<ProjectTaskAssignment> findByTaskId(Long taskId);

    // 특정 taskId에 대해 userIds 목록에 포함된 사용자들을 삭제
    void deleteByTaskIdAndUserIdIn(Long taskId, List<Long> userIds);

    // taskId에 할당된 모든 작업담당자 삭제
    void deleteByTaskId(Long taskId);

    // 특정 프로젝트의 특정 사용자에 대한 작업을 삭제하는 메서드
    @Modifying
    @Transactional
    @Query("DELETE FROM ProjectTaskAssignment pta WHERE pta.task.state.project.id = :projectId AND pta.user.id = :userId")
    void deleteByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    // userId로 삭제하기
    void deleteByUserId(Long userId);

}