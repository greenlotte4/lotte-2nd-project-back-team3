package BackAnt.repository;

import BackAnt.entity.approval.BusinessTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessTripRepository extends JpaRepository<BusinessTrip, Long> {
}