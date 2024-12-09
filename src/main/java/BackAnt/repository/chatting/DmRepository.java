package BackAnt.repository.chatting;

import BackAnt.entity.User;
import BackAnt.entity.chatting.Dm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DmRepository extends JpaRepository<Dm, Long> {
    // 사용자(User)가 포함된 DM 방을 찾는 메서드

    @Query("SELECT DISTINCT dm FROM Dm dm " +
            "JOIN DmMember dmm ON dmm.dm = dm " +
            "WHERE dmm.user IN :users")
    List<Dm> findAllByUsersIn(@Param("users") List<User> users);

    @Query("SELECT DISTINCT dm FROM Dm dm " +
            "JOIN DmMember dmm ON dmm.dm = dm " +
            "WHERE dmm.user.id = :userId")
    List<Dm> findAllByUserId(@Param("userId") Long userId);

}
