package BackAnt.repository;

import BackAnt.entity.Project;
import BackAnt.entity.ProjectCollaborator;
import BackAnt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
    날짜 : 2024/12/2
    이름 : 강은경
    내용 : ProjectCollaboratorRepository 생성
*/
@Repository
public interface ProjectCollaboratorRepository extends JpaRepository<ProjectCollaborator, Long> {


    // 특정 사용자와 관련된 ProjectCollaborator 조회
    @Query("SELECT pc.project FROM ProjectCollaborator pc WHERE pc.user.uid = :uid")
    List<Project> findProjectsByUserUid(@Param("uid") String uid);


}