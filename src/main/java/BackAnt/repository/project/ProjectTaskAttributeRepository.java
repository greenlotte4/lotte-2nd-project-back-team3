package BackAnt.repository.project;

import BackAnt.entity.User;
import BackAnt.entity.enums.AttributeType;
import BackAnt.entity.project.Project;
import BackAnt.entity.project.ProjectCollaborator;
import BackAnt.entity.project.ProjectTaskAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
    날짜 : 2024/12/22
    이름 : 강은경
    내용 : ProjectTaskAttributeRepository 생성
*/
@Repository
public interface ProjectTaskAttributeRepository extends JpaRepository<ProjectTaskAttribute, Long> {

    // 프로젝트 작업 속성 타입별 조회
    List<ProjectTaskAttribute> findByType(AttributeType type);


}