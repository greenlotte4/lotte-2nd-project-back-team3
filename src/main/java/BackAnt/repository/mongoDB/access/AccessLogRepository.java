package BackAnt.repository.mongoDB.access;

import BackAnt.entity.AccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessLogRepository extends MongoRepository<AccessLog, String> {
    List<AccessLog> findByUserId(String userId); // 사용자 ID로 로그 조회
    Page<AccessLog> findByUserIdContainingOrUrlPathContaining(String userId, String urlPath, Pageable pageable);

}
