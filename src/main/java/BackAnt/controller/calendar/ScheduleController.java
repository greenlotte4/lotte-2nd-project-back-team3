package BackAnt.controller.calendar;

import BackAnt.dto.calendar.CalendarDTO;
import BackAnt.dto.calendar.ScheduleDTO;
import BackAnt.service.calendar.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/insert")
    public void insert(@RequestBody ScheduleDTO scheduleDTO) {
        log.info("sch:::::::::::::"+scheduleDTO);

        calendarService.insertSchedule(scheduleDTO);

    }

}