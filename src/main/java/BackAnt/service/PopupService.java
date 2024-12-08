package BackAnt.service;

import BackAnt.entity.Popup;
import BackAnt.repository.PopupRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.stream.Collectors;

/*
    날 짜 : 2024/12/8(일)
    담당자 : 최준혁
    내 용 : Popup 를 위한 Service 생성
*/

@RequiredArgsConstructor
@Service
@Log4j2
public class PopupService {

    private final PopupRepository popupRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 회사별 팝업 데이터 가져오기
    public List<Popup> getPopupsByCompany(Long companyId) {
        return popupRepository.findByCompanyId(companyId);
    }

    public Popup createPopup(Popup popup) {
        return popupRepository.save(popup);
    }

    public Popup updatePopup(Long id, Popup popup) {
        Popup existingPopup = popupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Popup not found"));
        existingPopup.setTitle(popup.getTitle());
        existingPopup.setDescription(popup.getDescription());
        existingPopup.setStartDate(popup.getStartDate());
        existingPopup.setEndDate(popup.getEndDate());
        existingPopup.setActive(popup.isActive());
        existingPopup.setCompanyId(popup.getCompanyId()); // 회사 ID 설정
        return popupRepository.save(existingPopup);
    }

    public void deletePopup(Long id) {
        popupRepository.deleteById(id);
    }

    // 사용자에게 표시할 팝업 목록 필터링
    public List<Popup> getVisiblePopups(Long userId) {
        List<Popup> allPopups = popupRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        return allPopups.stream()
                .filter(popup -> popup.isActive() &&
                        popup.getStartDate().isBefore(now.toLocalDate()) &&
                        popup.getEndDate().isAfter(now.toLocalDate()) &&
                        !isPopupHidden(userId, popup.getId()))
                .collect(Collectors.toList());
    }

    // Redis에 팝업 숨김 상태 저장 (TTL 설정)
    public void hidePopup(Long userId, Long popupId) {
        String key = generateRedisKey(userId, popupId);
        System.out.println("Generated Redis Key: " + key); // 키 확인

        try {
            redisTemplate.opsForValue().set(key, true, Duration.ofDays(7)); // TTL 7일
            System.out.println("Key saved in Redis: " + key);
        } catch (Exception e) {
            System.err.println("Error saving key to Redis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Redis에서 팝업 숨김 상태 확인
    private boolean isPopupHidden(Long userId, Long popupId) {
        String key = generateRedisKey(userId, popupId);
        Boolean isHidden = redisTemplate.hasKey(key);
        log.info("Checking if Popup is hidden for Key: {} - Result: {}", key, isHidden);
        return Boolean.TRUE.equals(isHidden);
    }

    // Redis 키 생성
    private String generateRedisKey(Long userId, Long popupId) {
        return String.format("hidden_popups:%d:%d", userId, popupId);
    }
}
