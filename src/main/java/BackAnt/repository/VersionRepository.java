package BackAnt.repository;

import BackAnt.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionRepository extends JpaRepository<Version, Long> {
}
