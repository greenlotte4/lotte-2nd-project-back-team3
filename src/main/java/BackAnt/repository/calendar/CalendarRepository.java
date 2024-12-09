package BackAnt.repository.calendar;

import BackAnt.entity.calendar.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Integer> {

    List<Calendar> findAllByUser_Uid(String userId);
    List<Calendar> findAllByView(String view);
}
