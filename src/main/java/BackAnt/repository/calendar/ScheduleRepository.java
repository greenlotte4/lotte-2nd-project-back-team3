package BackAnt.repository.calendar;

import BackAnt.entity.calendar.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    List<Schedule> findByCalendarCalendarIdOrderByStartAsc(Integer calendarId);

    List<Schedule> findAllByCalendarCalendarId(Integer calendarId);

}
