package BackAnt.repository.page;

import BackAnt.entity.page.PageCollaborator;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageCollaboratorRepository extends JpaRepository<PageCollaborator, Integer> {
    List<PageCollaborator> findByPageId(String projectId);
    void deleteByPageIdAndUser_Id(String projectId, long userId);
    void deleteByPageId(String projectId);
    @Query("SELECT pc.pageId FROM PageCollaborator pc WHERE pc.user.uid = :userId AND pc.isOwner = false")
    List<String> findPageIdsByUserId(@Param("userId") String userId);
    List<PageCollaborator> findByUser_UidAndIsOwnerFalse(String userId);
    void deleteByUser_Id(long userId);
    Optional<PageCollaborator> findByPageIdAndUser_Id(String pageId, Long userId);
}
