package BackAnt.repository.chatting;

import BackAnt.entity.chatting.Dm;
import BackAnt.entity.chatting.DmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DmMessageRepository extends JpaRepository<DmMessage, Long> {
    List<DmMessage> findAllByDm(Dm dm);

    // 특정 DM 방에서 특정 사용자가 보낸 메시지 조회
    List<DmMessage> findAllByDmAndSenderId(Dm dm, Long senderId);

    // 특정 메시지를 ID로 삭제
    void deleteById(Long messageId);

}
