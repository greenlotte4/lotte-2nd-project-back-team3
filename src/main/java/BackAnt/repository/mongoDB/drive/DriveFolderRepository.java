package BackAnt.repository.mongoDB.drive;

import BackAnt.document.page.drive.DriveFolderDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriveFolderRepository extends MongoRepository<DriveFolderDocument, String> {

    @Query(value = "{ 'driveParentFolderId': null, 'driveFolderIsDeleted':0 }", fields = "{ 'driveFolderId': 1, 'driveFolderName': 1, 'driveParentFolderId': 1, 'driveFolderMaker': 1, 'driveFolderSize': 1, 'driveFolderCreatedAt': 1}")
    List<DriveFolderDocument> findFirstWithFolders();

    @Query(value = "{ 'driveFolderIsDeleted':1 }", fields = "{ 'driveFolderId': 1, 'driveFolderName': 1, 'driveParentFolderId': 1, 'driveFolderMaker': 1, 'driveFolderSize': 1, 'driveFolderCreatedAt': 1}")
    List<DriveFolderDocument> findFirstWithDeleteFolders();

    @Query(value = "{ 'driveParentFolderId': ?0 ,'driveFolderIsDeleted':0 }",
            fields = "{ 'driveFolderId': 1, 'driveFolderName': 1, 'driveParentFolderId': 1, 'driveFolderMaker': 1, 'driveFolderSize': 1, 'driveFolderCreatedAt': 1 }")
    List<DriveFolderDocument> findWithSelectFolders(String driveFolderId);

    @Query(value = "{ 'driveParentFolderId': ?0 ,'driveFolderIsDeleted':1 }",
            fields = "{ 'driveFolderId': 1, 'driveFolderName': 1, 'driveParentFolderId': 1, 'driveFolderMaker': 1, 'driveFolderSize': 1, 'driveFolderCreatedAt': 1 }")
    List<DriveFolderDocument> findWithSelectDeleteFolders(String driveFolderId);


    @Query(value = "{ 'driveFolderId': ?0 }", fields = "{ 'driveFolderName': 1, 'driveFolderId': 1, 'driveParentFolderId': 1, 'driveFolderPath': 1 }")
    Optional<DriveFolderDocument> finddriveFolderNameById(String driveFolderId);

    List<DriveFolderDocument> findBydriveFolderPathStartingWith(String path);



}
