package BackAnt.repository;


import BackAnt.entity.approval.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VactionRepository extends JpaRepository<Vacation, Long> {
}