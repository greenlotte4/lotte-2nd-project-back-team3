package BackAnt.service.calendar;

import BackAnt.controller.calendar.ScheduleController;
import BackAnt.dto.UserDTO;
import BackAnt.dto.calendar.CalendarDTO;
import BackAnt.dto.calendar.ScheduleDTO;
import BackAnt.entity.User;
import BackAnt.entity.calendar.Calendar;
import BackAnt.entity.calendar.Schedule;
import BackAnt.entity.calendar.ViewCalendar;
import BackAnt.repository.UserRepository;
import BackAnt.repository.calendar.CalendarRepository;
import BackAnt.repository.calendar.ScheduleRepository;
import BackAnt.repository.calendar.ViewCalendarRepository;
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
    private final ViewCalendarRepository viewCalendarRepository;


    public List<CalendarDTO> selectCalendar (String uid){

        log.info("유아이디"+uid);

        List<ViewCalendar> cIds = viewCalendarRepository.findByUserId(Long.parseLong(uid));

        List<Integer> calendarIds = new ArrayList<>();

        cIds.forEach(viewCalendar -> {
            calendarIds.add(viewCalendar.getCalendar().getCalendarId());
        });

        log.info("캘린더번호"+calendarIds);

        List<Calendar> calendars = calendarRepository.findAllById(calendarIds);

        log.info("45678"+calendars);

        return calendars.stream()
                .map(calendar -> {
                    CalendarDTO calendarDTO = modelMapper.map(calendar, CalendarDTO.class);
                    calendarDTO.setUser_id(calendar.getUser() != null ? calendar.getUser().getUid() : null);
                    return calendarDTO;
                })
                .toList();
    }

    public List<CalendarDTO> selectCalendarModal (String uid){

        log.info("유아이디"+uid);

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
                .color(calendarDTO.getColor())
                .build();



        log.info("5555"+calendar);

        Calendar calendar123 = calendarRepository.save(calendar);

        ViewCalendar view = ViewCalendar.builder()
                .user(calendar.getUser())
                .calendar(calendar123)
                .build();

        viewCalendarRepository.save(view);
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

        log.info("유아이디값이 머징" + uid);

        List<ViewCalendar> cIds = viewCalendarRepository.findByUserId(Long.parseLong(uid));

        List<Integer> calIds = new ArrayList<>();

        cIds.forEach(viewCalendar -> {
            calIds.add(viewCalendar.getCalendar().getCalendarId());
        });

        log.info("캘린더번호"+calIds);

        List<Calendar> calendarIds = calendarRepository.findAllById(calIds);


        List<Integer> Ids = new ArrayList<>();

        calendarIds.forEach(calendar -> {
            Ids.add(calendar.getCalendarId());
        });

        log.info("iddddddddddddd"+cIds);


        List<Schedule> scheduless = new ArrayList<>();

        Ids.forEach(cId -> {
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

    public void shareCalendar (String id, String ids) {

        String cleanedStr = ids.replaceAll("[\\[\\]\\s]", "");
        log.info("666666666666666666666666666"+cleanedStr);

        List<String> lists = Arrays.asList(cleanedStr.split(","));
        log.info("666666666666666666666666666"+lists);

        lists.forEach(list -> {
            ViewCalendar viewCalendar = ViewCalendar.builder()
                    .calendar(calendarRepository.findById(Integer.parseInt(id)).orElseThrow(() -> new EntityNotFoundException("이 id의 Schedule 이 없습니다.")))
                    .user(userRepository.findById(Long.parseLong(list)).orElseThrow(() -> new EntityNotFoundException("이 id의 Schedule 이 없습니다.")))
                    .build();
            viewCalendarRepository.save(viewCalendar);
        });

    }

    public List<UserDTO> selectShare (String id) {

        List<ViewCalendar> calendars = viewCalendarRepository.findByCalendar_CalendarId(Integer.parseInt(id));

        List<Long> ids = new ArrayList<>();

        calendars.forEach(viewCalendar -> {
            ids.add(viewCalendar.getUser().getId());
        });


        List<User> users = userRepository.findAllById(ids);

        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteShare (String cId, String uId) {

        ViewCalendar viewCalendar = viewCalendarRepository.findByCalendar_CalendarIdAndUserId(Integer.parseInt(cId), Long.parseLong(uId));

        log.info(viewCalendar);

        viewCalendarRepository.delete(viewCalendar);

    }

}
