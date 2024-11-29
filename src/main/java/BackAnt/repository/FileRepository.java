package BackAnt.repository;

/*
    날 짜 : 2024/11/28(목)
    담당자 : 황수빈
    내 용 : File 을 위한 Repository 생성
*/

import BackAnt.entity.FileEntity;
import BackAnt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Integer> {
}
