package BackAnt.controller;

import BackAnt.entity.AccessLog;
import BackAnt.service.AccessLogService;
import BackAnt.service.kafka.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/logs")
public class AccessLogController {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogController.class);
    private final AccessLogService accessLogService;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    // 로그 생성 API
    @PostMapping
    public String createLog(@RequestBody AccessLog log) {
        try {
            String message = objectMapper.writeValueAsString(log);
            kafkaProducerService.sendMessage("access-log-topic", message);
            return "Log sent to Kafka!";
        } catch (Exception e) {
            logger.error("Error sending log to Kafka", e);
            return "Error sending log to Kafka: " + e.getMessage();
        }
    }

    // 사용자 접근 시 로그 기록 후 Kafka 전송
    @PostMapping("/log")
    public String logAccess(
            @RequestParam String userId,
            @RequestParam String urlPath,
            @RequestParam String httpMethod,
            @RequestParam String ipAddress) {
        try {
            AccessLog log = new AccessLog(userId, ipAddress, urlPath, httpMethod, LocalDateTime.now(), null);
            String message = objectMapper.writeValueAsString(log);
            kafkaProducerService.sendMessage("access-log-topic", message);
            return "Access log sent to Kafka!";
        } catch (Exception e) {
            logger.error("Error sending access log to Kafka", e);
            return "Error sending access log to Kafka: " + e.getMessage();
        }
    }

    // 로그 조회 (페이징 포함)
    @GetMapping("/search")
    public Page<AccessLog> getAccessLogs(
            @RequestParam(value = "search", required = false) String searchTerm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("이건가/.");
        Pageable pageable = PageRequest.of(page, size);
        return accessLogService.getAccessLogs(searchTerm, pageable);
    }

    // 특정 사용자 로그 조회
    @GetMapping("/user/{userId}")
    public List<String> getLogsByUserId(@PathVariable String userId) {
        log.info("444455556666" + userId);
        return accessLogService.getLogsByUserId(userId);
    }

    // 모든 로그 조회 API
    @GetMapping("/all")
    public List<AccessLog> getAllLogs() {
        return accessLogService.getAllLogs();
    }

    // ID로 특정 로그 조회 API
    @GetMapping("/{id}")
    public AccessLog getLogById(@PathVariable String id) {
        return accessLogService.getLogById(id);
    }

    // ID로 로그 삭제 API
    @DeleteMapping("/{id}")
    public String deleteLog(@PathVariable String id) {
        accessLogService.deleteLog(id);
        return "Log deleted successfully!";
    }
}