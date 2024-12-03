package BackAnt.controller.calendar;

import BackAnt.dto.calendar.CalendarDTO;
import BackAnt.service.calendar.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    날짜 : 2024/12/02
    이름 : 하정훈
    내용 : Calendar controller 생성
*/

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {


    private final CalendarService calendarService;

    @GetMapping("/select/{uid}")
    public List<CalendarDTO> calendarList(@PathVariable String uid){
        log.info("44444"+uid);
        log.info("123543"+calendarService.selectCalendar(uid));
        return calendarService.selectCalendar(uid);
    }

    @PostMapping("/insert")
    public String calendar(@RequestBody CalendarDTO calendar) {

        log.info("44444"+calendar);

        calendarService.insertCalendar(calendar);


        return null;
    }

}
