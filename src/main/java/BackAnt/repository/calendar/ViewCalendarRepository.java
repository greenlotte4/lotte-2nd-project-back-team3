package BackAnt.repository.calendar;

import BackAnt.entity.calendar.ViewCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewCalendarRepository extends JpaRepository<ViewCalendar, Integer> {

    List<ViewCalendar> findByUserId(Long user_id);

    List<ViewCalendar> findByCalendar_CalendarId(int calendar_calendarId);  // 수정된 부분

    ViewCalendar findByCalendar_CalendarIdAndUserId(int calendar_calendarId, Long user_id);

    long countByCalendar_CalendarId(int calendar_calendarId);

    void deleteByUserId(Long userId);

}
