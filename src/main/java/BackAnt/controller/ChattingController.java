package BackAnt.controller;

import BackAnt.dto.chatting.*;
import BackAnt.service.chatting.ChannelMessageService;
import BackAnt.service.chatting.ChannelService;
import BackAnt.service.chatting.DmService;
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
    private final DmService dmService;

    // 채널 생성
    @PostMapping("/channel")
    public ResponseEntity<Void> createChannel(@RequestBody ChannelCreateDTO channelCreateDTO) {
        channelService.createChannel(channelCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 모든 채널 조회
    @GetMapping("/channel")
    public ResponseEntity<List<ChannelResponseDTO>> getAllChannels() {
        List<ChannelResponseDTO> channels = channelService.getAllChannels();
        return ResponseEntity.status(HttpStatus.OK).body(channels);
//        return new ResponseEntity<>(channels, HttpStatus.OK);
    }

    // 채널 ID로 채널 조회
    @GetMapping("/channel/{id}")
    public ResponseEntity<ChannelResponseDTO> getChannelById(@PathVariable Long id) {
        ChannelResponseDTO channel = channelService.getChannel(id);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    // 채널 멤버 추가
    @PostMapping("/channel/{id}/member")
    public ResponseEntity<Void> addChannelMember(@PathVariable Long id, @RequestBody ChannelMemberAddDTO channelMemberAddDTO) {
        channelService.addChannelMember(id, channelMemberAddDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 채널 메시지 보내기
    @PostMapping("/channel/{id}/message")
    public ResponseEntity<Void> sendMessage(@PathVariable Long id, @RequestBody ChannelMessageCreateDTO channelMessageCreateDTO) {
        channelMessageService.sendMessage(channelMessageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 메시지 검색 (채널 내 키워드로)
    @GetMapping("/channel/{channelId}/messages")
    public ResponseEntity<List<ChannelMessageResponseDTO>> getMessages(@PathVariable Long channelId, @RequestParam String keyword) {
        // 채널 ID와 키워드를 기준으로 메시지 검색
        List<ChannelMessageResponseDTO> messages = channelMessageService.getsearchChannelMessages(channelId, keyword);
        return ResponseEntity.ok(messages);
    }

    // 채널 멤버 삭제
    @DeleteMapping("/channel/{id}/member")
    public ResponseEntity<Void> removeChannelMember(@PathVariable Long id, @RequestBody ChannelMemberAddDTO channelMemberAddDTO) {
        channelService.removeChannelMember(id, channelMemberAddDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/channel/{channelId}/leave")
    public ResponseEntity<Void> transferOwnershipAndLeave(
            @PathVariable Long channelId,
            @RequestParam Long userId
    ) {
        channelService.transferOwnershipAndLeave(channelId, userId);
        return ResponseEntity.ok().build();
    }

    // 디엠방 생성 (1:1 비공개 채팅)
    @PostMapping("/dm")
    public ResponseEntity<DmResponseDTO> createDm(@RequestBody DmCreateDTO dmCreateDTO, @RequestParam Long senderId) {
        // DM 방 생성 및 첫 번째 메시지 처리
        DmResponseDTO response = dmService.createDm(dmCreateDTO, senderId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 디엠 메시지 보내기
    @PostMapping("/dm/{dmId}/messages")
    public ResponseEntity<Void> sendDmMessage(@PathVariable Long dmId, @RequestParam Long senderId, @RequestBody DmMessageCreateDTO dmMessageCreateDTO) {
        // DM 메시지 보내기
        dmService.sendMessage(dmId, senderId, dmMessageCreateDTO.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
