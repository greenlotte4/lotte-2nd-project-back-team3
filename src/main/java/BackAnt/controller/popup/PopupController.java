package BackAnt.controller.popup;

import BackAnt.entity.Popup;
import BackAnt.service.PopupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
    날 짜 : 2024/12/8(일)
    담당자 : 최준혁
    내 용 : Popup 를 위한 Controller 생성
*/
@Log4j2
@RestController
@RequestMapping("/api/popups")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    // 회사별 팝업 데이터 가져오기
    @GetMapping
    public List<Popup> getPopupsByCompany(@RequestParam Long companyId) {
        return popupService.getPopupsByCompany(companyId);
    }

    // 팝업 생성
    @PostMapping
    public Popup createPopup(@RequestBody Popup popup) {
        return popupService.createPopup(popup);
    }

    // 팝업 업데이트
    @PutMapping("/{id}")
    public Popup updatePopup(@PathVariable Long id, @RequestBody Popup popup) {
        return popupService.updatePopup(id, popup);
    }

    // 팝업 삭제
    @DeleteMapping("/{id}")
    public void deletePopup(@PathVariable Long id) {
        popupService.deletePopup(id);
    }

    // 사용자에게 표시될 팝업 목록
    @GetMapping("/visible/{userId}")
    public List<Popup> getVisiblePopups(@PathVariable Long userId) {
        log.info("Fetching visible popups for userId: {}", userId);
        return popupService.getVisiblePopups(userId);
    }
    // 팝업 숨김 요청 처리
    @PostMapping("/{popupId}/hide")
    public ResponseEntity<Void> hidePopup(@PathVariable Long popupId, @RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        log.info("요청 들어오니?");
        popupService.hidePopup(userId, popupId);
        return ResponseEntity.ok().build();
    }

}
