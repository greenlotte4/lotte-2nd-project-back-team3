package BackAnt.service.calendar;

import BackAnt.dto.NotificationDTO;
import BackAnt.dto.user.UserDTO;
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
import BackAnt.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public List<CalendarDTO> selectCalendar (String uid){


        List<ViewCalendar> cIds = viewCalendarRepository.findByUserId(Long.parseLong(uid));

        List<Integer> calendarIds = new ArrayList<>();

        cIds.forEach(viewCalendar -> {
            calendarIds.add(viewCalendar.getCalendar().getCalendarId());
        });


        List<Calendar> calendars = calendarRepository.findAllById(calendarIds);


        List<CalendarDTO> dtos = calendars.stream()
                .map(calendar -> {
                    CalendarDTO calendarDTO = modelMapper.map(calendar, CalendarDTO.class);
                    calendarDTO.setUser_id(calendar.getUser() != null ? calendar.getUser().getUid() : null);
                    return calendarDTO;
                })
                .toList();

        dtos.forEach(dto -> {
            long result = viewCalendarRepository.countByCalendar_CalendarId(dto.getCalendarId());
            if(result == 1){
                dto.setShare(false);
            } else if (result > 1){
                dto.setShare(true);
            }
        });

        return dtos;
    }

    public List<CalendarDTO> selectCalendarModal (String uid){


        List<Calendar> calendars = calendarRepository.findAllByUser_Uid(uid);


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

        Calendar calendar123 = calendarRepository.save(calendar);

        ViewCalendar view = ViewCalendar.builder()
                .user(calendar.getUser())
                .calendar(calendar123)
                .build();

        viewCalendarRepository.save(view);
    }

    public CalendarDTO updateCalendar (int no, String newName, String color) {
        Calendar calendar = calendarRepository.findById(no).orElseThrow(() -> new EntityNotFoundException("이 id의 Calendar가 없습니다."));

        List<Schedule> schedules = scheduleRepository.findAllByCalendarCalendarId(no);

        if(Objects.equals(color, "not")){
            calendar.updateName(newName);
        }else {
            calendar.update(newName, color);
        }

        calendarRepository.save(calendar);
        List<ScheduleDTO> dtos = schedules.stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleDTO.class)) // 매핑 실행
                .toList();

        dtos.forEach(dto -> {
            dto.setColor(calendar.getColor());
            dto.setAction("update");
            dto.setCalendarId(calendar.getCalendarId());
            List<ViewCalendar> viewCalendars = viewCalendarRepository.findByCalendar_CalendarId(calendar.getCalendarId());
            // 2. WebSocket을 통한 실시간 알림 전송
            viewCalendars.forEach(viewCalendar -> {

                String destination = "/topic/schedules/" + viewCalendar.getUser().getId();
                messagingTemplate.convertAndSend(destination, dto);
            });
        });


        Calendar calendar1 = calendarRepository.save(calendar);
       return modelMapper.map(calendar1, CalendarDTO.class);

    }

    public void deleteCalendar (int no) {
        calendarRepository.deleteById(no);
    }

    public ScheduleDTO insertSchedule (ScheduleDTO scheduleDTO){

        String internalAttendees = scheduleDTO.getInternalAttendees().toString() .replace("[", "")
                .replace("]", "");;
        String externalAttendees = scheduleDTO.getExternalAttendees().toString() .replace("[", "")
                .replace("]", "");;

         Calendar calendar = calendarRepository.findById(scheduleDTO.getCalendarId()).orElseThrow(() -> new EntityNotFoundException("이 id의 Calendar가 없습니다."));

        Schedule schedule = Schedule.builder()
                .title(scheduleDTO.getTitle())
                .calendar(calendar)
                .content(scheduleDTO.getContent())
                .internalAttendees(internalAttendees)
                .externalAttendees(externalAttendees)
                .location(scheduleDTO.getLocation())
                .start(scheduleDTO.getStart())
                .end(scheduleDTO.getEnd())
                .build();

       Schedule schedule1 =  scheduleRepository.save(schedule);

       ScheduleDTO dto = modelMapper.map(schedule1, ScheduleDTO.class);

       dto.setColor(calendar.getColor());

       dto.setAction("insert");
        dto.setCalendarId(calendar.getCalendarId());
       List<ViewCalendar> viewCalendars = viewCalendarRepository.findByCalendar_CalendarId(calendar.getCalendarId());
        // 2. WebSocket을 통한 실시간 알림 전송
        viewCalendars.forEach(viewCalendar -> {
            String destination = "/topic/schedules/" + viewCalendar.getUser().getId();
            messagingTemplate.convertAndSend(destination, dto);
        });


       return dto;

    }

    public List<ScheduleDTO> selectSchedule (String uid) {


        List<ViewCalendar> cIds = viewCalendarRepository.findByUserId(Long.parseLong(uid));

        List<Integer> calIds = new ArrayList<>();

        cIds.forEach(viewCalendar -> {
            calIds.add(viewCalendar.getCalendar().getCalendarId());
        });


        List<Calendar> calendarIds = calendarRepository.findAllById(calIds);


        List<Integer> Ids = new ArrayList<>();

        calendarIds.forEach(calendar -> {
            Ids.add(calendar.getCalendarId());
        });



        List<Schedule> scheduless = new ArrayList<>();

        Ids.forEach(cId -> {
            List<Schedule> sch = scheduleRepository.findByCalendarCalendarIdOrderByStartAsc(cId);
            scheduless.addAll(sch);
        });
        scheduless.sort(Comparator.comparing(Schedule::getStart));


        return scheduless.stream()
                .map(schedule -> {
                    ScheduleDTO dto = modelMapper.map(schedule, ScheduleDTO.class); // 기본 매핑
                    dto.setCalendarId(schedule.getCalendar().getCalendarId()); // calendarId 수동 설정
                    dto.setColor(schedule.getCalendar().getColor());
                    return dto;
                })
                .toList();
    }

    public ScheduleDTO selectScheduleDetail (int id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("이 id의 schedule 이 없습니다."));

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

        return dto;

    }

    public ScheduleDTO updateSchedule (int no, LocalDateTime start, LocalDateTime end) {

        Schedule schedule = scheduleRepository.findById(no).orElseThrow(() -> new EntityNotFoundException("이 id의 Schedule 이 없습니다."));


        schedule.updateTime(start, end);

        Schedule schedule1 = scheduleRepository.save(schedule);

        ScheduleDTO dto = modelMapper.map(schedule1, ScheduleDTO.class);
        dto.setAction("update");



        List<ViewCalendar> viewCalendars = viewCalendarRepository.findByCalendar_CalendarId(schedule1.getCalendar().getCalendarId());
        // 2. WebSocket을 통한 실시간 알림 전송
        viewCalendars.forEach(viewCalendar -> {
            dto.setColor((calendarRepository.findById(viewCalendar.getCalendar().getCalendarId()).orElseThrow(() -> new EntityNotFoundException("이 id의 Schedule 이 없습니다."))).getColor());
            dto.setCalendarId(viewCalendar.getCalendar().getCalendarId());
            String destination = "/topic/schedules/" + viewCalendar.getUser().getId();
            messagingTemplate.convertAndSend(destination, dto);
        });

        return null;
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
        Schedule schedule = scheduleRepository.findById(no).orElse(null);
        scheduleRepository.deleteById(no);
        ScheduleDTO dto = modelMapper.map(schedule, ScheduleDTO.class);
        dto.setAction("delete");
        List<ViewCalendar> viewCalendars = viewCalendarRepository.findByCalendar_CalendarId(schedule.getCalendar().getCalendarId());
        viewCalendars.forEach(viewCalendar -> {
            String destination = "/topic/schedules/" + viewCalendar.getUser().getId();
            messagingTemplate.convertAndSend(destination, dto);
        });
    }

    public void shareCalendar (String id, String ids) {

        String cleanedStr = ids.replaceAll("[\\[\\]\\s]", "");

        List<String> lists = Arrays.asList(cleanedStr.split(","));

        lists.forEach(list -> {
            ViewCalendar viewCalendar = ViewCalendar.builder()
                    .calendar(calendarRepository.findById(Integer.parseInt(id)).orElseThrow(() -> new EntityNotFoundException("이 id의 Schedule 이 없습니다.")))
                    .user(userRepository.findById(Long.parseLong(list)).orElseThrow(() -> new EntityNotFoundException("이 id의 Schedule 이 없습니다.")))
                    .build();
            viewCalendarRepository.save(viewCalendar);
            // WebSocket을 통한 실시간 알림 전송
            NotificationDTO notification = NotificationDTO.builder()
                    .targetType("USER")
                    .targetId(Long.parseLong(list)) // Approver ID
                    .senderId(Long.parseLong(id))
                    .message(viewCalendar.getCalendar().getName() + " 캘린더를 공유받으셨습니다!")
                    .metadata(Map.of(
                            "type", "공유캘린더초대"
                    ))
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationService.createAndSendNotification(notification);
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

    public void settingLanguage(Long id, String language){

        User user = userRepository.findById(id).orElse(null);

        user.updateCalendarLanguage(language);

        userRepository.save(user);

    }

}
