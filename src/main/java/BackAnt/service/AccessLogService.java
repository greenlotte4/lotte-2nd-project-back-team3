package BackAnt.service;

import BackAnt.entity.AccessLog;
import BackAnt.repository.mongoDB.access.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    // 로그 생성
    public void saveLog(AccessLog accessLog) {
        try {
            AccessLog savedLog = accessLogRepository.save(accessLog);
            System.out.println("Saved log to MongoDB: " + savedLog);
        } catch (Exception e) {
            System.err.println("Error saving to MongoDB: " + e.getMessage());
            throw e;
        }
    }

    // 모든 로그 조회
    public List<AccessLog> getAllLogs() {
        return accessLogRepository.findAll();
    }
    // 로그 조회 페이징
    public Page<AccessLog> getAccessLogs(String searchTerm, Pageable pageable) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            log.info("Search term: " + searchTerm);
            Page<AccessLog> result = accessLogRepository.findByUserIdContainingOrUrlPathContainingOrderByAccessTimeDesc(searchTerm, searchTerm, pageable);
            log.info("Logs found with search term: " + result.getContent());
            return result;
        }
        Page<AccessLog> result = accessLogRepository.findAllByOrderByAccessTimeDesc(pageable);
        log.info("All logs: " + result.getContent());
        return result;
    }

    // 사용자별 조회
    public List<String> getLogsByUserId(String userId) {
        List<AccessLog> accessLogs = accessLogRepository.findByUserIdOrderByAccessTimeDesc(userId);

        log.info("Logs:::::::::::: " + accessLogs);
        List<String> filteredParts = accessLogs.stream()
                .map(AccessLog::getUrlPath) // Extract urlPath
                .map(urlPath -> urlPath.replaceFirst("^/api/", "")) // Remove "/api/"
                .map(strippedPath -> strippedPath.split("/")[0]) // Get the first part after "/api/"
                .filter(part -> !part.equals("user")) // Exclude "user"
                .distinct()
                .toList();

        log.info("7777777777777" + filteredParts);
        return filteredParts;
    }


    // ID로 특정 로그 조회
    public AccessLog getLogById(String id) {
        return accessLogRepository.findById(id).orElse(null);
    }

    // ID로 로그 삭제
    public void deleteLog(String id) {
        accessLogRepository.deleteById(id);
    }
}