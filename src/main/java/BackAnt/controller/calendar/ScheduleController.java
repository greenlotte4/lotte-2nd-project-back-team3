package BackAnt.controller.calendar;

import BackAnt.dto.calendar.CalendarDTO;
import BackAnt.dto.calendar.ScheduleDTO;
import BackAnt.service.DepartmentService;
import BackAnt.service.UserService;
import BackAnt.service.calendar.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/*
    날짜 : 2024/12/04
    이름 : 하정훈
    내용 : Schedule controller 생성
*/

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final CalendarService calendarService;
    private final DepartmentService departmentService;

    @PostMapping("/insert")
    public ScheduleDTO insertSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        log.info(scheduleDTO);
       return calendarService.insertSchedule(scheduleDTO);
    }

    @GetMapping("/select/{uid}")
    public List<ScheduleDTO> select(@PathVariable String uid) {

        return calendarService.selectSchedule(uid);

    }

    @GetMapping("/select/detail/{id}")
    public ScheduleDTO selectDetail(@PathVariable int id) {

        log.info("fffffffffffffff:::"+id);

        log.info("dddddddddddddddddddd"+calendarService.selectScheduleDetail(id));
        return calendarService.selectScheduleDetail(id);

    }

    @GetMapping("/selectDepart/{department}")
    public List<String> selectDepart(@PathVariable String department) {
        log.info("dep:::::::::::::"+department);

        return departmentService.selectDepart(department);

    }

    @PutMapping("/update/{no}/{start}/{end}")
    public void update(@PathVariable int no, @PathVariable String start, @PathVariable String end) {

        log.info("11111111111"+no);


        start = start.replaceAll("\\s*\\(.*\\)$", ""); // "(한국 표준시)" 부분 제거
        end = end.replaceAll("\\s*\\(.*\\)$", "");

        log.info("22222222222"+start);
        log.info("333333333333"+end);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);

        ZonedDateTime zonedStart = ZonedDateTime.parse(start, formatter);
        ZonedDateTime zonedEnd = ZonedDateTime.parse(end, formatter);

        LocalDateTime startTime = zonedStart.toLocalDateTime();
        LocalDateTime endTime = zonedEnd.toLocalDateTime();

        log.info("22222222222"+startTime);
        log.info("333333333333"+endTime);
        calendarService.updateSchedule(no, startTime, endTime);
    }

    @PutMapping("/update/detail")
    public void update(@RequestBody ScheduleDTO scheduleDTO) {
        log.info("11111111111::::::::::::"+scheduleDTO);

        calendarService.updateScheduleDetail(scheduleDTO);

    }

    @DeleteMapping("/delete/{no}")
    public void deleteSchedule(@PathVariable int no) {
        log.info("11111111111"+no);
        calendarService.deleteSchedule(no);
    }

}
