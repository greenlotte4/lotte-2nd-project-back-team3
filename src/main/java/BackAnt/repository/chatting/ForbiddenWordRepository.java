package BackAnt.repository.chatting;

import BackAnt.entity.chatting.ForbiddenWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForbiddenWordRepository extends JpaRepository<ForbiddenWord, Long> {
}