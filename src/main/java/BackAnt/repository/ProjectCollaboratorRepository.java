package BackAnt.repository;

import BackAnt.entity.Project;
import BackAnt.entity.ProjectCollaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
    날짜 : 2024/12/2
    이름 : 강은경
    내용 : ProjectCollaboratorRepository 생성
*/
@Repository
public interface ProjectCollaboratorRepository extends JpaRepository<ProjectCollaborator, Long> {

    // uid로 관련 프로젝트 조회
    List<ProjectCollaborator> findByUserUid(String uid);


}