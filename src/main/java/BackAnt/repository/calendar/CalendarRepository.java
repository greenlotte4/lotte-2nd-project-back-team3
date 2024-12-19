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
    void deleteByUser_Uid(String userId);
}
