package BackAnt.service.calendar;

import BackAnt.controller.calendar.ScheduleController;
import BackAnt.dto.calendar.CalendarDTO;
import BackAnt.dto.calendar.ScheduleDTO;
import BackAnt.entity.calendar.Calendar;
import BackAnt.entity.calendar.Schedule;
import BackAnt.repository.UserRepository;
import BackAnt.repository.calendar.CalendarRepository;
import BackAnt.repository.calendar.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                .color(calendarDTO.getColor())
                .build();

        log.info("5555"+calendar);

        calendarRepository.save(calendar);
    }

    public void updateCalendar (int no, String newName, String color) {
        Calendar calendar = calendarRepository.findById(no).orElseThrow(() -> new EntityNotFoundException("이 id의 Calendar가 없습니다."));

        log.info("123123432432"+calendar);
        if(Objects.equals(color, "not")){
            calendar.updateName(newName);
        }else {
            calendar.update(newName, color);
        }
        log.info("123123432432"+calendar);
        calendarRepository.save(calendar);

    }

    public void deleteCalendar (int no) {
        calendarRepository.deleteById(no);
    }

    public void insertSchedule (ScheduleDTO scheduleDTO){

        String internalAttendees = scheduleDTO.getInternalAttendees().toString() .replace("[", "")
                .replace("]", "");;
        String externalAttendees = scheduleDTO.getExternalAttendees().toString() .replace("[", "")
                .replace("]", "");;

        Schedule schedule = Schedule.builder()
                .title(scheduleDTO.getTitle())
                .calendar(calendarRepository.findById(scheduleDTO.getCalendarId()).orElseThrow(() -> new EntityNotFoundException("이 id의 Calendar가 없습니다.")))
                .content(scheduleDTO.getContent())
                .internalAttendees(internalAttendees)
                .externalAttendees(externalAttendees)
                .location(scheduleDTO.getLocation())
                .start(scheduleDTO.getStart())
                .end(scheduleDTO.getEnd())
                .build();

        scheduleRepository.save(schedule);

    }

    public List<ScheduleDTO> selectSchedule (String uid) {

        List<Calendar> calendarIds = calendarRepository.findAllByView(uid);

        List<Integer> cIds = new ArrayList<>();

        calendarIds.forEach(calendar -> {
            cIds.add(calendar.getCalendarId());
        });

        log.info("iddddddddddddd"+cIds);


        List<Schedule> scheduless = new ArrayList<>();

        cIds.forEach(cId -> {
            List<Schedule> sch = scheduleRepository.findByCalendarCalendarIdOrderByStartAsc(cId);
            scheduless.addAll(sch);
        });

        log.info("cccccccccccccccccccccccccccc"+ scheduless);


        return scheduless.stream()
                .map(schedule -> {
                    ScheduleDTO dto = modelMapper.map(schedule, ScheduleDTO.class); // 기본 매핑
                    dto.setCalendarId(schedule.getCalendar().getCalendarId()); // calendarId 수동 설정
                    dto.setColor(schedule.getCalendar().getColor());
                    log.info("5555"+dto);
                    return dto;
                })
                .toList();
    }

    public ScheduleDTO selectScheduleDetail (int id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("이 id의 schedule 이 없습니다."));
        log.info("777777777777777777777::::::::::"+schedule);

        List<String> internal = Arrays.stream(schedule.getInternalAttendees().split(","))
                .map(String::trim)  // 각 항목에서 공백 제거
                .toList();

        List<String> external = Arrays.stream(schedule.getExternalAttendees().split(","))
                .map(String::trim)  // 각 항목에서 공백 제거
                .toList();
        ScheduleDTO dto = modelMapper.map(schedule, ScheduleDTO.class);

        dto.setInternalAttendees(internal);
        dto.setExternalAttendees(external);
        dto.setCalendarId(schedule.getCalendar().getCalendarId());
        log.info("8888888888888888:::"+dto);

        return dto;

    }

    public void updateSchedule (int no, LocalDateTime start, LocalDateTime end) {

        Schedule schedule = scheduleRepository.findById(no).orElseThrow(() -> new EntityNotFoundException("이 id의 Schedule 이 없습니다."));


        schedule.updateTime(start, end);

        scheduleRepository.save(schedule);

    }
    public void updateScheduleDetail (ScheduleDTO scheduleDTO) {

        String internalAttendees = scheduleDTO.getInternalAttendees().toString() .replace("[", "")
                .replace("]", "");;
        String externalAttendees = scheduleDTO.getExternalAttendees().toString() .replace("[", "")
                .replace("]", "");;

        Schedule schedule = scheduleRepository.findById(scheduleDTO.getId()).orElseThrow(() -> new EntityNotFoundException("이 id의 Schedule 이 없습니다."));
        modelMapper.map(scheduleDTO, schedule);

        schedule.updateAttendees(internalAttendees, externalAttendees);

        scheduleRepository.save(schedule);
    }

    public void deleteSchedule (int no) {
        scheduleRepository.deleteById(no);
    }

}
