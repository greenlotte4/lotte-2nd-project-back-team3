package BackAnt.aspect;

import BackAnt.JWT.JwtProvider;
import BackAnt.config.MethodDescriptionConfig;
import BackAnt.entity.AccessLog;
import BackAnt.service.kafka.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AccessLogAspect {


    private final HttpServletRequest request;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    // 캘린더 관련 API
    @Pointcut("execution(* BackAnt.controller.calendar.CalendarController.calendar(..)) || " +
            "execution(* BackAnt.controller.calendar.CalendarController.delete(..)) || " +
            "execution(* BackAnt.controller.calendar.CalendarController.updateShare(..)) || " +
            "execution(* BackAnt.controller.calendar.CalendarController.deleteShare(..))")
    public void calendarMethods() {}

    // 일정 관련 API
    @Pointcut("execution(* BackAnt.controller.calendar.ScheduleController.insert(..)) || " +
            "execution(* BackAnt.controller.calendar.ScheduleController.delete(..))")
    public void scheduleMethods() {}

    // 프로젝트 관련 API
    @Pointcut("execution(* BackAnt.controller.project.ProjectController.createProject(..))")
    public void projectMethods() {}

    // 프로젝트 협업자 관련 API
    @Pointcut("execution(* BackAnt.controller.project.ProjectCollaboratorController.addCollaboratorToProject(..)) || " +
            "execution(* BackAnt.controller.project.ProjectCollaboratorController.removeProjectCollaborator(..))")
    public void collaboratorMethods() {}

    // 프로젝트 상태 관련 API
    @Pointcut("execution(* BackAnt.controller.project.ProjectStateController.addState(..)) || " +
            "execution(* BackAnt.controller.project.ProjectStateController.deleteState(..))")
    public void stateMethods() {}

    // 프로젝트 작업 관련 API
    @Pointcut("execution(* BackAnt.controller.project.ProjectTaskController.createTask(..)) || " +
            "execution(* BackAnt.controller.project.ProjectTaskController.deleteTaskById(..))")
    public void taskMethods() {}

    // 페이지 관련 API
    @Pointcut("execution(* BackAnt.controller.page.PageController.createPage(..)) || " +
            "execution(* BackAnt.controller.page.PageController.softDeletePageById(..)) || " +
            "execution(* BackAnt.controller.page.PageController.addCollaborators(..)) || " +
            "execution(* BackAnt.controller.page.PageController.removeCollaborator(..))")
    public void pageMethods() {}

    // 드라이브 관련 API
    @Pointcut("execution(* BackAnt.controller.drive.DriveController.folderInsert(..)) || " +
            "execution(* BackAnt.controller.drive.DriveController.filesInsert(..)) || " +
            "execution(* BackAnt.controller.drive.DriveController.MyDriveFileDownload(..)) ")

    public void driveMethods() {}

    // 채팅 관련 API
    @Pointcut("execution(* BackAnt.controller.ChattingController.createChannel(..)) || " +
            "execution(* BackAnt.controller.ChattingController.transferOwnershipAndLeave(..)) || " +
            "execution(* BackAnt.controller.ChattingController.getAllChannels(..)) || " +
            "execution(* BackAnt.controller.ChattingController.getChannelById(..)) || " +
            "execution(* BackAnt.controller.ChattingController.getChannelMembers(..)) || " +
            "execution(* BackAnt.controller.ChattingController.sendMessage(..)) || " +
            "execution(* BackAnt.controller.ChattingController.getMessages(..)) || " +
            "execution(* BackAnt.controller.ChattingController.markMessagesAsRead(..)) || " +
            "execution(* BackAnt.controller.ChattingController.searchMessages(..))")
    public void chattingMethods() {}

    // DM 관련 API
    @Pointcut("execution(* BackAnt.controller.ChattingController.createDm(..)) || " +
            "execution(* BackAnt.controller.ChattingController.getDm(..)) || " +
            "execution(* BackAnt.controller.ChattingController.getDmMessages(..)) || " +
            "execution(* BackAnt.controller.ChattingController.sendDmMessage(..)) || " +
            "execution(* BackAnt.controller.ChattingController.markDmMessagesAsRead(..))")
    public void dmMethods() {}

    // 실행할 메서드 결합
    @Pointcut("calendarMethods() || scheduleMethods() || projectMethods() || collaboratorMethods() || " +
            "stateMethods() || taskMethods() || pageMethods() || driveMethods() || chattingMethods() || dmMethods()")
    public void specificApiMethods() {}

    // 특정 메서드 실행 후 로그 기록
    @After("specificApiMethods()")
    public void logAccess(JoinPoint joinPoint) {
        try {
            String authHeader = request.getHeader("Authorization");
            String userId = "unknown";
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Claims claims = jwtProvider.getClaims(token);
                userId = claims.get("uid", String.class);
            }

            // 접근 로그 생성
            AccessLog log = new AccessLog();
            log.setUserId(userId);
            log.setIpAddress(request.getRemoteAddr());
            log.setUrlPath(request.getRequestURI());
            log.setHttpMethod(request.getMethod());
            log.setAccessTime(LocalDateTime.now());
            log.setMethodDescription(MethodDescriptionConfig.getDescription(joinPoint.getSignature().getName()));

            // JSON 직렬화 및 Kafka 전송
            String message = objectMapper.writeValueAsString(log);
            kafkaProducerService.sendMessage("access-log-topic", message);
        } catch (Exception e) {
            System.err.println("로그 생성 실패: " + e.getMessage());
        }
    }
}