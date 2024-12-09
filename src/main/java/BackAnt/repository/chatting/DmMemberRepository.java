package BackAnt.repository.chatting;

import BackAnt.entity.chatting.Dm;
import BackAnt.entity.chatting.DmMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DmMemberRepository extends JpaRepository<DmMember, Long> {

    @Query("SELECT dmm FROM DmMember dmm WHERE dmm.dm = :dm")
    List<DmMember> findAllByDm(@Param("dm") Dm dm);
}
