package BackAnt.repository.landing;

import BackAnt.entity.landing.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {
    List<Qna> findByEmail(String email);

}
