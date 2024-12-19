package BackAnt.repository.mongoDB.access;

import BackAnt.entity.AccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessLogRepository extends MongoRepository<AccessLog, String> {
    List<AccessLog> findByUserIdOrderByAccessTimeDesc(String userId); // 사용자 ID로 로그 조회
    Page<AccessLog> findByUserIdContainingOrUrlPathContaining(String userId, String urlPath, Pageable pageable);

    // 검색어가 있을 경우 + 내림차순
    Page<AccessLog> findByUserIdContainingOrUrlPathContainingOrderByAccessTimeDesc(
            String userId, String urlPath, Pageable pageable);

    // 검색어가 없을 경우 모든 로그를 accessTime 내림차순으로 반환
    Page<AccessLog> findAllByOrderByAccessTimeDesc(Pageable pageable);

}
