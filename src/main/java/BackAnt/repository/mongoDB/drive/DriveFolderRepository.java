package BackAnt.repository.mongoDB.drive;

import BackAnt.document.page.drive.DriveFolderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriveFolderRepository extends MongoRepository<DriveFolderDocument, String> {

    @Query(value = "{}", fields = "{ 'driveFolderId': 1, 'driveFolderName': 1, 'driveParentFolderId': 1, 'driveFolderMaker': 1, 'driveFolderSize': 1, 'driveFolderCreatedAt': 1}")
    List<DriveFolderDocument> findAllWithFolders();

    @Query(value = "{ 'driveParentFolderId': ?0 }",
            fields = "{ 'driveFolderId': 1, 'driveFolderName': 1, 'driveParentFolderId': 1, 'driveFolderMaker': 1, 'driveFolderSize': 1, 'driveFolderCreatedAt': 1 }")
    List<DriveFolderDocument> findWithSelectFolders(String driveFolderId);
}
