package BackAnt.controller;

import BackAnt.dto.chatting.*;
import BackAnt.dto.common.ResultDTO;
import BackAnt.entity.chatting.Channel;
import BackAnt.entity.chatting.ChannelMessage;
import BackAnt.service.chatting.ChannelMessageService;
import BackAnt.service.chatting.ChannelService;
import BackAnt.service.chatting.DmService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatting")
@Log4j2
public class ChattingController {
    private final ChannelService channelService;
    private final ChannelMessageService channelMessageService;
    private final DmService dmService;

    // 채널 생성
    @PostMapping("/channel")
    public ResponseEntity<ResultDTO<Long>> createChannel(@RequestBody ChannelCreateDTO channelCreateDTO) {
        log.info("여기는 컨트롤러");
        Long channelId = channelService.createChannel(channelCreateDTO);

        log.info("채널 생성 정보 : "+channelCreateDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResultDTO.<Long>builder().data(channelId).build());
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
    @PostMapping("/channel/{id}/messages")
    public ResponseEntity<Void> sendMessage(@PathVariable Long id, @RequestBody ChannelMessageCreateDTO channelMessageCreateDTO) {
        channelMessageService.sendMessage(id ,channelMessageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 메시지 검색 (채널 내 키워드로)
    @GetMapping("/channel/{channelId}/messages/search")
    public ResponseEntity<List<ChannelMessageResponseDTO>> searchMessages(@PathVariable Long channelId, @RequestParam String keyword) {
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

    // 채널 나가기
    @PutMapping("/channel/{channelId}/leave")
    public ResponseEntity<Void> transferOwnershipAndLeave(
            @PathVariable Long channelId,
            @RequestParam Long userId
    ) {
        channelService.transferOwnershipAndLeave(channelId, userId);
        return ResponseEntity.ok().build();
    }

    // 채널 메시지 읽음 처리
    @PostMapping("/channel/{channelId}/messages/read")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable Long channelId, @RequestParam Long userId) {
        channelMessageService.markMessagesAsRead(channelId, userId);
        return ResponseEntity.ok().build();
    }

    // 채널 메시지 조회
    @GetMapping("/channel/{channelId}/messages")
    public ResponseEntity<List<ChannelMessageResponseDTO>> getMessages(@PathVariable Long channelId) {
        List<ChannelMessageResponseDTO> messages = channelMessageService.getMessages(channelId);
        return ResponseEntity.ok(messages); // 상태 코드 200과 함께 메시지 리스트 반환
    }



    // 디엠방 생성 (1:1 비공개 채팅)
    @PostMapping("/dm")
    public ResponseEntity<ResultDTO<Long>> createDm(@RequestBody DmCreateDTO dmCreateDTO) {
        // DM 방 생성 및 첫 번째 메시지 처리
        ResultDTO<Long> response = dmService.createDm(dmCreateDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 디엠방 가져오기 by userId
    @GetMapping("/dm")
    public ResponseEntity<List<DmResponseDTO>> getDm(@RequestParam Long userId)
    {
        List<DmResponseDTO> dmResponseDTOs = dmService.getDmsByUserId(userId);
        return new ResponseEntity<>(dmResponseDTOs, HttpStatus.OK);
    }

    // 디엠 메시지 보내기
    @PostMapping("/dm/{dmId}/messages")
    public ResponseEntity<Void> sendDmMessage(@PathVariable Long dmId, @RequestParam Long senderId, @RequestBody DmMessageCreateDTO dmMessageCreateDTO) {
        // DM 메시지 보내기
        dmService.sendMessage(dmId, senderId, dmMessageCreateDTO.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 디엠 메시지 조회
    @GetMapping("/dm/{dmId}/messages")
    public ResponseEntity<List<DmMessageResponseDTO>> getDmMessages(@PathVariable Long dmId) {
        List<DmMessageResponseDTO> messages = dmService.getMessages(dmId);
        return ResponseEntity.ok(messages);
    }

    // 디엠 메시지 읽음 표시
    @PatchMapping("/dm/{dmId}/messages/read")
    public ResponseEntity<String> markDmMessagesAsRead(@PathVariable Long dmId, @RequestBody DmReadRequestDTO dto) {
        dmService.markMessagesAsRead(dmId, dto.getUserId());
        return ResponseEntity.ok("메시지가 성공적으로 읽음으로 표시되었습니다.");
    }

}
