package BackAnt.repository.drive;

import BackAnt.entity.drive.DriveFileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriveFileStoarageRepository extends JpaRepository<DriveFileStorage, Integer> {

    DriveFileStorage findByUserId(String userId);
}
