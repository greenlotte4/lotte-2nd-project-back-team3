package BackAnt.repository.drive;

import BackAnt.entity.drive.DriveIsStared;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriveIsStaredRepository extends JpaRepository<DriveIsStared, Integer> {

    Optional<DriveIsStared> findByUserIdAndDriveFolderIdAndDriveFileId(String userId, String driveFolderId, int driveFileId);
    List<DriveIsStared> findByUserId(String userId);
}
