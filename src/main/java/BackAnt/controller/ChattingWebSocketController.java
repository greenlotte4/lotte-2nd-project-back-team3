package BackAnt.controller;

import BackAnt.dto.chatting.ChannelMessageCreateDTO;
import BackAnt.dto.chatting.ChannelMessageResponseDTO;
import BackAnt.dto.chatting.ChannelMessageSocketDTO;
import BackAnt.dto.chatting.DmMessageSocketDTO;
import BackAnt.service.chatting.ChannelMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class ChattingWebSocketController {

    private final ChannelMessageService channelMessageService;


    // 채널 메시지 전송
    @MessageMapping("/chatting/channel/{id}/send")
    @SendTo("/topic/chatting/channel/{id}/messages")  // 경로 통일
    public ChannelMessageSocketDTO sendChannelMessage(
            @DestinationVariable("id") Long id,
            @Payload ChannelMessageSocketDTO messageDTO
    ) {
        log.info("채널 {}로 메시지 수신: {}", id, messageDTO);

        return messageDTO;
    }

    // 디엠 메시지 전송
    @MessageMapping("/chatting/dm/{id}/send")
    @SendTo("/topic/chatting/dm/{id}/messages")  // 경로 통일
    public DmMessageSocketDTO sendDmMessage(
            @DestinationVariable("id") Long id,
            @Payload DmMessageSocketDTO messageDTO
    ) {
        log.info("dm {}로 메시지 수신: {}", id, messageDTO);

        return messageDTO;
    }
}