package BackAnt.controller;

import BackAnt.dto.chatting.ChannelCreateDTO;
import BackAnt.dto.chatting.ChannelMemberAddDTO;
import BackAnt.dto.chatting.ChannelMessageCreateDTO;
import BackAnt.dto.chatting.ChannelResponseDTO;
import BackAnt.service.chatting.ChannelMessageService;
import BackAnt.service.chatting.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatting")
public class ChattingController {
    private final ChannelService channelService;
    private final ChannelMessageService channelMessageService;

    @PostMapping("/channel")
    public ResponseEntity<Void> createChannel(@RequestBody ChannelCreateDTO channelCreateDTO) {
        channelService.createChannel(channelCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/channel")
    public ResponseEntity<List<ChannelResponseDTO>> getAllChannels() {
        List<ChannelResponseDTO> channels = channelService.getAllChannels();
        return ResponseEntity.status(HttpStatus.OK).body(channels);
//        return new ResponseEntity<>(channels, HttpStatus.OK);
    }

    // /api/chatting/channel/13
    @GetMapping("/channel/{id}")
    public ResponseEntity<ChannelResponseDTO> getChannelById(@PathVariable Long id) {
        ChannelResponseDTO channel = channelService.getChannel(id);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    @PostMapping("/channel/{id}/member")
    public ResponseEntity<Void> addChannelMember(@PathVariable Long id, @RequestBody ChannelMemberAddDTO channelMemberAddDTO) {
        channelService.addChannelMember(id, channelMemberAddDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/channel/{id}/member")
    public ResponseEntity<Void> removeChannelMember(@PathVariable Long id, @RequestBody ChannelMemberAddDTO channelMemberAddDTO) {
        channelService.removeChannelMember(id, channelMemberAddDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 메시지 보내기
    @PostMapping("/channel/{id}/message")
    public ResponseEntity<Void> sendMessage(@PathVariable Long id, @RequestBody ChannelMessageCreateDTO channelMessageCreateDTO) {
        channelMessageService.sendMessage(channelMessageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
