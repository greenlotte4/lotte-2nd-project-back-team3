package BackAnt.controller;

import BackAnt.dto.chatting.ChannelCreateDTO;
import BackAnt.dto.common.ResponseDTO;
import BackAnt.service.chatting.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatting")
public class ChattingController {
    private final ChannelService channelService;

    @PostMapping("/channel")
    public ResponseEntity<ResponseDTO<Long>> createChannel(@RequestBody ChannelCreateDTO channelCreateDTO) {
        Long channelId = channelService.createChannel(channelCreateDTO);
        ResponseDTO<Long> result = ResponseDTO.success(channelId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
