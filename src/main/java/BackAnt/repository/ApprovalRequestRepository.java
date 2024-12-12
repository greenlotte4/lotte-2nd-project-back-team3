package BackAnt.repository;

import BackAnt.entity.approval.ApprovalRequest;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    List<ApprovalRequest> findByUserId(Long userId);
    List<ApprovalRequest> findByTypeAndStatus(String type, String status);

    //////////////////// 페이징 ////////////////////////////
    Page<ApprovalRequest> findAllByUserId(Long userId, Pageable pageable);

    Page<ApprovalRequest> findAllByUserIdAndStatus(Long userId, String status, Pageable pageable);

    Page<ApprovalRequest> findAllByUserIdAndType(Long userId, String type, Pageable pageable);

    @Query("SELECT a FROM ApprovalRequest a WHERE a.userId = :userId AND a.status = :status AND a.type = :type")
    Page<ApprovalRequest> findAllByUserIdAndFilters(Long userId, String status, String type, Pageable pageable);
}
