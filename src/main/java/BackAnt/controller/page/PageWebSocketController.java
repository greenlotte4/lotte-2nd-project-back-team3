package BackAnt.controller.page;

import BackAnt.dto.page.PageDTO;
import BackAnt.service.page.PageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor
public class PageWebSocketController {

    private final PageService pageService; // 페이지 서비스 주입 필요


    @MessageMapping("/page/{id}")  // /app/page/{id}로 메시지가 전송됨
    @SendTo({"/topic/page/{id}"})    // 구독자들에게 브로드캐스트
    public PageDTO handlePageUpdate(
            @DestinationVariable String id,
            @Payload PageDTO pageDTO  // @Payload 어노테이션 추가
    ) {
        log.info("Received WebSocket message for page: " + id);
        log.info("Message content: " + pageDTO);

        try {
            pageService.updatePageInRealTime(pageDTO);
            return pageDTO;  // 다른 클라이언트들에게 변경사항 브로드캐스트
        } catch (Exception e) {
            log.error("Error handling WebSocket message: ", e);
            throw e;
        }
    }

    @MessageExceptionHandler
    @SendTo("/topic/errors")
    public String handleException(Throwable exception) {
        log.error("WebSocket Error: ", exception);
        return "Error: " + exception.getMessage();
    }
}