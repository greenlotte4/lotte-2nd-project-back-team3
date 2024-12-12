package BackAnt.repository;

import BackAnt.entity.User;
import BackAnt.entity.approval.Approver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApproverRepository extends JpaRepository<Approver, Long> {
    Optional<Approver> findByUser(User user);
}
