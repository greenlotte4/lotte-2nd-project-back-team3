package BackAnt.repository.chatting;

import BackAnt.entity.User;
import BackAnt.entity.chatting.Dm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DmRepository extends JpaRepository<Dm, Long> {
    // 사용자(User)가 포함된 DM 방을 찾는 메서드
    List<Dm> findByMembersContaining(User user);

}
