package BackAnt.repository;

/*
    날 짜 : 2024/11/28(목)
    담당자 : 황수빈
    내 용 : File 을 위한 Repository 생성
*/

import BackAnt.entity.DriveFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriveFileRepository extends JpaRepository<DriveFileEntity, Integer> {


    // folderId가 null인 행을 조회하는 메서드
    List<DriveFileEntity> findBydriveFolderIdIsNull();
}
