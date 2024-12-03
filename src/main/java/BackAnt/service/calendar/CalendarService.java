package BackAnt.service.calendar;

import BackAnt.dto.calendar.CalendarDTO;
import BackAnt.entity.calendar.Calendar;
import BackAnt.repository.UserRepository;
import BackAnt.repository.calendar.CalendarRepository;
import BackAnt.repository.calendar.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class CalendarService {

    private final ModelMapper modelMapper;
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    public List<CalendarDTO> selectCalendar (String uid){

        List<Calendar> calendars = calendarRepository.findAllByUser_Uid(uid);

        log.info("45678"+calendars);

        return calendars.stream()
                .map(calendar -> {
                    CalendarDTO calendarDTO = modelMapper.map(calendar, CalendarDTO.class);
                    calendarDTO.setUser_id(calendar.getUser() != null ? calendar.getUser().getUid() : null);
                    return calendarDTO;
                })
                .toList();
    }

    public void insertCalendar (CalendarDTO calendarDTO) {

        Calendar calendar = Calendar.builder()
                .name(calendarDTO.getName())
                .user(userRepository.findByUid(calendarDTO.getUser_id()).orElseThrow(() -> new EntityNotFoundException("user값이 없습니다.")))
                .view(calendarDTO.getUser_id())
                .build();

        log.info("5555"+calendar);

        calendarRepository.save(calendar);
    }

    public void insertSchedule (){

    }

}
