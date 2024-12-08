package BackAnt.repository.mongoDB.page;
import BackAnt.document.page.PageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
    날 짜 : 2024/11/28(목)
    담당자 : 황수빈
    내 용 : Page 를 위한 Repository 생성
*/

@Repository
public interface PageRepository extends MongoRepository<PageDocument, String> {
    List<PageDocument> findByOwnerAndDeletedAtIsNull(String uid);
    List<PageDocument> findByOwnerAndDeletedAtIsNotNull(String uid);
    List<PageDocument> findTop3ByDeletedAtIsNullOrderByUpdatedAtDesc();
}
