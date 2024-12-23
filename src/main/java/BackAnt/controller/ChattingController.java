package BackAnt.controller;

import BackAnt.dto.chatting.*;
import BackAnt.dto.common.ResultDTO;
import BackAnt.entity.chatting.Channel;
import BackAnt.entity.chatting.ChannelMessage;
import BackAnt.repository.chatting.ChannelMessageRepository;
import BackAnt.entity.chatting.DmMessage;
import BackAnt.repository.chatting.ChannelRepository;
import BackAnt.repository.chatting.DmMessageRepository;
import BackAnt.service.chatting.ChannelMessageService;
import BackAnt.service.chatting.ChannelService;
import BackAnt.service.chatting.DmService;
import BackAnt.service.chatting.ForbiddenWordService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    private final ChannelMessageRepository channelMessageRepository;
    private final DmMessageRepository dmMessageRepository;
    private final ForbiddenWordService forbiddenWordService;

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
    public ResponseEntity<List<ChannelResponseDTO>> getAllChannels(Long memberId) {
        List<ChannelResponseDTO> channels = channelService.getVisibleChannel(memberId);
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
    public ResponseEntity<List<ChannelMemberResponseDTO>> addChannelMember(@PathVariable Long id, @RequestBody ChannelMemberAddDTO channelMemberAddDTO) {
        List<ChannelMemberResponseDTO> channelMemberResponseDTOS = channelService.addChannelMember(id, channelMemberAddDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelMemberResponseDTOS);
    }

    // 파일 전송
    @PostMapping("/channel/{id}/files")
    public ResponseEntity<ChannelMessageResponseDTO> sendMessageWithFile(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "senderId") Long senderId) {

        try {
            Long channelMessageId = channelMessageService.sendMessageWithFile(id, senderId, content, file);
            ChannelMessage message = channelMessageRepository.findById(channelMessageId)
                    .orElseThrow(() -> new RuntimeException("Message not found"));

            // ForbiddenWordService를 함께 전달
            ChannelMessageResponseDTO responseDTO = ChannelMessageResponseDTO.fromEntity(message, forbiddenWordService);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("메시지 전송 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // 채널 메시지 보내기
    @PostMapping("/channel/{id}/messages")
    public ResponseEntity<ChannelMessageResponseDTO> sendMessage(@PathVariable Long id, @RequestBody ChannelMessageCreateDTO channelMessageCreateDTO) {
        ChannelMessageResponseDTO channelMessageResponseDTO = channelMessageService.sendMessage(id, channelMessageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelMessageResponseDTO);
    }

    // 금칙어 추가
    @PostMapping("/forbidden-words")
    public ResponseEntity<ForbiddenWordDTO> addForbiddenWord(@RequestBody ForbiddenWordDTO dto) {
        ForbiddenWordDTO addedWord = forbiddenWordService.addForbiddenWord(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedWord);
    }

    // 금칙어 조회
    @GetMapping("/forbidden-words")
    public ResponseEntity<List<ForbiddenWordDTO>> getForbiddenWords() {
        List<ForbiddenWordDTO> words = forbiddenWordService.getAllForbiddenWords();
        return ResponseEntity.ok(words);
    }

    // 금칙어 삭제
    @DeleteMapping("/forbidden-words/{id}")
    public ResponseEntity<Void> deleteForbiddenWord(@PathVariable Long id) {
        forbiddenWordService.deleteForbiddenWord(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 메시지 필터링 테스트
    @PostMapping("/test-filter-message")
    public ResponseEntity<String> testFilterMessage(@RequestBody String message) {
        String filteredMessage = forbiddenWordService.filterMessage(message);
        return ResponseEntity.ok(filteredMessage);
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


    // 채널 메시지 조회
    @GetMapping("/channel/{channelId}/messages")
    public ResponseEntity<List<ChannelMessageResponseDTO>> getMessages(@PathVariable Long channelId) {
        List<ChannelMessageResponseDTO> messages = channelMessageService.getMessages(channelId);
        return ResponseEntity.ok(messages); // 상태 코드 200과 함께 메시지 리스트 반환
    }

    // 채널 멤버 조회
    @GetMapping("/channel/{channelId}/members")
    public ResponseEntity<List<ChannelMemberResponseDTO>> getChannelMembers(@PathVariable Long channelId) {
        List<ChannelMemberResponseDTO> members = channelService.getChannelMembers(channelId);
        return ResponseEntity.ok(members);
    }

    // 채널 이름 수정
    @PatchMapping("/channel/{channelId}/title")
    public ResponseEntity<Void> changeChannelTitle(@PathVariable Long channelId, @RequestBody ChannelChangeNameDTO channelChangeNameDTO)
    {
        channelService.changeChannelTitle(channelId, channelChangeNameDTO.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 채널 검색
    @GetMapping("/channel/search")
    public ResponseEntity<List<ChannelResponseDTO>> searchVisibleChannelsByName(
            @RequestParam("memberId") Long memberId,
            @RequestParam(value = "channelName", required = false) String channelName) {

        List<ChannelResponseDTO> result = channelService.getVisibleChannelByName(memberId, channelName);
        return ResponseEntity.ok(result);
    }

    // 특정 메시지의 unreadCount 조회
    @GetMapping("/channel/{channelId}/messages/{messageId}/unreadCount")
    public long getUnreadCount(@PathVariable Long channelId, @PathVariable Long messageId) {
        return channelMessageService.getUnreadCount(channelId, messageId);
    }

    // 특정 채널에 사용자가 방문했을 때 lastReadAt을 갱신하는 엔드포인트
    @PostMapping("/channel/{channelId}/members/{memberId}/visit")
    public void updateLastReadAtOnVisit(@PathVariable Long channelId,
                                        @PathVariable Long memberId) {
        // 현재 시간을 lastReadAt으로 기록
        LocalDateTime now = LocalDateTime.now();
        channelMessageService.updateLastReadAt(channelId, memberId, now);
    }

    @GetMapping("/channel/{channelId}/unreadCount")
    public ResponseEntity<Long> getChannelUnreadCount(@PathVariable Long channelId, @RequestParam Long userId) {
        long unreadCount = channelService.getUnreadCountByChannelIdAndMemberId(channelId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(unreadCount);
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

    // 디엠 id로 디엠방 조회
    @GetMapping("/dm/{dmId}")
    public ResponseEntity<DmResponseDTO> getDmById(@PathVariable Long dmId) {
        DmResponseDTO dmResponse = dmService.getDmById(dmId);
        return ResponseEntity.ok(dmResponse);
    }

    // 디엠 메시지 보내기
    @PostMapping("/dm/{dmId}/messages")
    public ResponseEntity<ResultDTO<Long>> sendDmMessage(@PathVariable Long dmId, @RequestBody DmMessageCreateDTO dmMessageCreateDTO) {
        // DM 메시지 보내기
        Long messageId = dmService.sendMessage(dmId, dmMessageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResultDTO<>(messageId));
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

    @GetMapping("/dm/{dmId}/members")
    public ResponseEntity<List<DmMemberResponseDTO>> getDmMembers(@PathVariable Long dmId) {
        List<DmMemberResponseDTO> members = dmService.getDmMembers(dmId);
        return ResponseEntity.ok(members);
    }


    // 메시지 삭제 API
    @DeleteMapping("/dm/messages/{messageId}")
    public ResponseEntity<String> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId
    ) {
        dmService.checkAndDeleteMessage(messageId, userId);
        return ResponseEntity.ok("메시지가 삭제되었습니다.");
    }


}
