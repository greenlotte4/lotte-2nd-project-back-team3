package BackAnt.repository.drive;

import BackAnt.entity.drive.DriveCollaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriveCollaboratorRepository extends JpaRepository<DriveCollaborator, Integer> {

    // 프로젝트 ID를 기준으로 협업자 목록 조회
    @Query("SELECT d FROM DriveCollaborator d WHERE d.driveFolderId = :driveFolderId")
    List<DriveCollaborator> findByDriveFolderIdWithQuery(String driveFolderId);

    Optional<DriveCollaborator> findByDriveFolderIdAndUserId(String driveFolderId, Long userId);

    @Query("SELECT d.driveFolderId FROM DriveCollaborator d " + "WHERE d.isSharePoint = true " + "AND d.isOwner = false " + "AND d.user.id = :userId")
    List<String> findDriveFolderIdsByConditions(@Param("userId") Long userId);

    void deleteByUserId(Long userId);
}
