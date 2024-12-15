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

//    @Query(value = "{ 'driveFolderIsDeleted':1 }", fields = "{ 'driveFolderId': 1, 'driveFolderName': 1, 'driveParentFolderId': 1, 'driveFolderMaker': 1, 'driveFolderSize': 1, 'driveFolderCreatedAt': 1, 'driveFolderPath': 1 , 'driveFolderIsDeleted': 1}")
//    List<DriveFolderDocument> findFirstWithDeleteFolders();


//    // 삭제된 최상위 폴더 조회 (parentFolderId가 null인 경우)
//    @Query("{ 'driveFolderIsDeleted': 1, 'driveParentFolderId': null }")
//    List<DriveFolderDocument> findDeletedTopLevelFolders();
//
//    // 삭제된 하위 폴더 조회 (parentFolderId가 있는 경우)
//    @Query("{ 'driveFolderIsDeleted': 1, 'driveParentFolderId': { $ne: null } }")
//    List<DriveFolderDocument> findDeletedSubFolders();

    // 삭제된 모든 폴더 조회
    @Query("{ 'driveFolderIsDeleted': 1 }")
    List<DriveFolderDocument> findAllDeletedFolders();

    @Query(value = "{ 'driveParentFolderId': ?0 ,'driveFolderIsDeleted':0 }",
            fields = "{ 'driveFolderId': 1, 'driveFolderName': 1, 'driveParentFolderId': 1, 'driveFolderMaker': 1, 'driveFolderSize': 1, 'driveFolderCreatedAt': 1 }")
    List<DriveFolderDocument> findWithSelectFolders(String driveFolderId);

    @Query(value = "{ 'driveParentFolderId': ?0 ,'driveFolderIsDeleted':1 }",
            fields = "{ 'driveFolderId': 1, 'driveFolderName': 1, 'driveParentFolderId': 1, 'driveFolderMaker': 1, 'driveFolderSize': 1, 'driveFolderCreatedAt': 1 }")
    List<DriveFolderDocument> findWithSelectDeleteFolders(String driveFolderId);


    @Query(value = "{ 'driveFolderId': ?0 }", fields = "{ 'driveFolderName': 1, 'driveFolderId': 1, 'driveParentFolderId': 1, 'driveFolderPath': 1 }")
    Optional<DriveFolderDocument> finddriveFolderNameById(String driveFolderId);

    List<DriveFolderDocument> findBydriveFolderPathStartingWith(String path);

    Optional<DriveFolderDocument> findBydriveFolderPath(String folderPath);


    boolean existsByDriveFolderPathStartingWithAndDriveFolderIsDeleted(String Path, boolean isDeleted); // 하위 폴더 존재 여부 확인




}
