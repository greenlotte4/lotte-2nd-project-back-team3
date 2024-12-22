package BackAnt.controller;

import BackAnt.dto.chatting.ChannelMessageCreateDTO;
import BackAnt.dto.chatting.ChannelMessageResponseDTO;
import BackAnt.dto.chatting.ChannelMessageSocketDTO;
import BackAnt.dto.chatting.DmMessageSocketDTO;
import BackAnt.service.chatting.ChannelMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class ChattingWebSocketController {

    private final ChannelMessageService channelMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    // 채널 메시지 전송
    @MessageMapping("/chatting/channel/{id}/send")
    @SendTo("/topic/chatting/channel/{id}/messages")
    public ChannelMessageSocketDTO sendChannelMessage(
            @DestinationVariable("id") Long id,
            @Payload ChannelMessageSocketDTO messageDTO
    ) {
        log.info("채널 {}로 메시지 수신: {}", id, messageDTO);

        // 메시지 데이터 확장 (이미지 여부 및 파일 유형 확인)
        if (messageDTO.getFileUrl() != null) {
            String fileUrl = messageDTO.getFileUrl().toLowerCase();

            // 이미지 여부 설정
            boolean isImage = fileUrl.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$");
            messageDTO.setIsImage(isImage);

            // MIME 타입 기반으로 파일 유형 설정
            if (isImage) {
                messageDTO.setFileType("image");
            } else if (fileUrl.matches(".*\\.(pdf)$")) {
                messageDTO.setFileType("pdf");
            } else {
                messageDTO.setFileType("file");
            }
        }

        return messageDTO;
    }

    
    // TODO: 이 소켓 잘 안됨 받아는 지는데 보내는게 안되는듯 그래서 서비스에서 소켓 보냄
    // 채널 방문
    @MessageMapping("/chatting/channel/{id}/visit")
    @SendTo("/topic/chatting/channel/{id}/visit")
    public String visitChannel(
            @DestinationVariable("id") Long id
    ) {
        log.info("채널 {}로 방문", id);
//        messagingTemplate.convertAndSend("/topic/chatting/channel/" + id + "/visit", "채널 방문 메세지");
        return "채널 " + id + " 방문 메세지";
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