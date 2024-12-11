package BackAnt.controller;

import BackAnt.dto.chatting.ChannelMessageCreateDTO;
import BackAnt.dto.chatting.ChannelMessageResponseDTO;
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
    public ChannelMessageResponseDTO sendChannelMessage(
            @DestinationVariable Long id,
            @Payload ChannelMessageCreateDTO messageDTO
    ) {
        log.info("채널 {}로 메시지 수신: {}", id, messageDTO);

        // 메시지 저장
        channelMessageService.sendMessage(id, messageDTO);

        // 메시지 브로드캐스트
        ChannelMessageResponseDTO response = ChannelMessageResponseDTO.builder()
                .channelId(id)
                .content(messageDTO.getContent())
                .senderId(messageDTO.getSenderId())
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();

        log.info("브로드캐스트 메시지: {}", response);
        return response;
    }

    // 채널 메시지 조회
    @MessageMapping("/chatting/channel/{id}/messages")
    @SendTo("/topic/chatting/channel/{id}/messages")
    public List<ChannelMessageResponseDTO> getChannelMessages(@DestinationVariable Long id) {
        log.info("채널 {}의 메시지 요청", id);
        return channelMessageService.getMessages(id);
    }
}