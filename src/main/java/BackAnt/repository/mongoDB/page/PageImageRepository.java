package BackAnt.repository.mongoDB.page;

/*
    날 짜 : 2024/11/28(목)
    담당자 : 황수빈
    내 용 : Page 를 위한 Repository 생성
*/

import BackAnt.document.page.PageImageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageImageRepository extends MongoRepository<PageImageDocument, String> {
}
