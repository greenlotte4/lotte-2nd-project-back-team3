package BackAnt.repository.calendar;

import BackAnt.entity.calendar.Calendar;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Integer> {

    List<Calendar> findAllByUser_Uid(String userId);

    @Query("SELECT c FROM Calendar c WHERE c.view LIKE %:view%")
    List<Calendar> findAllByViewLike(@Param("view") String view);
}
