package BackAnt.controller.calendar;

import BackAnt.dto.user.UserDTO;
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
        List<CalendarDTO> dtos = calendarService.selectCalendar(uid);
        log.info("디티오"+dtos);
        return dtos;
    }

    @GetMapping("/select/modal/{uid}")
    public List<CalendarDTO> calendarModalList(@PathVariable String uid){
        return calendarService.selectCalendarModal(uid);
    }

    @GetMapping("/select/share/{uid}")
    public List<UserDTO> calendarshareList(@PathVariable String uid){
        log.info("공유캘린더 아이디는???"+uid);
        return calendarService.selectShare(uid);
    }

    @PostMapping("/insert")
    public void calendar(@RequestBody CalendarDTO calendar) {
        log.info("13245666");
        calendarService.insertCalendar(calendar);
    }

    @PutMapping("/update/{no}/{newName}/{color}")
    public CalendarDTO update(@PathVariable int no, @PathVariable String newName, @PathVariable String color) {

        return calendarService.updateCalendar(no, newName, color);
    }

    @DeleteMapping("/delete/{no}")
    public void deleteCalendar(@PathVariable int no) {

        calendarService.deleteCalendar(no);
    }

    @PutMapping("/share/{id}")
    public void updateShare(@PathVariable String id, @RequestBody String userIds) {

        log.info("아이아이아이아이아이"+id);
        log.info("아이아이아이아이아이"+userIds);

        calendarService.shareCalendar(id, userIds);
    }

    @DeleteMapping("/delete/share/{id}/{userId}")
    public void deleteShare(@PathVariable String id, @PathVariable String userId) {
        log.info("2222222222222222"+ id);
        log.info("2222222222222222"+ userId);

        calendarService.deleteShare(id, userId);

    }

    @PutMapping("/language/{id}/{language}")
    public void settingLanguage(@PathVariable Long id, @PathVariable String language){

        log.info("111:::"+id+"::"+language);

        calendarService.settingLanguage(id, language);

    }
}
