package BackAnt.repository;

import BackAnt.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {
    Optional<Invite> findByInviteToken(String inviteToken);
    List<Invite> findByDepartmentId(Long departmentId);

}
