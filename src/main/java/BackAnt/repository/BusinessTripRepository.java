package BackAnt.repository;

import BackAnt.entity.approval.BusinessTrip;
import BackAnt.entity.approval.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessTripRepository extends JpaRepository<BusinessTrip, Long> {

    List<BusinessTrip> findByStatus(String status);

}