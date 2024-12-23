package BackAnt.service.chatting;

import BackAnt.dto.chatting.ChannelMessageCreateDTO;
import BackAnt.dto.chatting.ChannelMessageResponseDTO;
import BackAnt.entity.User;
import BackAnt.entity.chatting.Channel;
import BackAnt.entity.chatting.ChannelMember;
import BackAnt.entity.chatting.ChannelMessage;
import BackAnt.repository.UserRepository;
import BackAnt.repository.chatting.ChannelMemberRepository;
import BackAnt.repository.chatting.ChannelMessageRepository;
import BackAnt.repository.chatting.ChannelRepository;
import BackAnt.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelMessageService {
    private final ChannelMessageRepository channelMessageRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ImageService imageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ForbiddenWordService forbiddenWordService;

    // 채널 메시지 보내기
    public ChannelMessageResponseDTO sendMessage(Long id, ChannelMessageCreateDTO dto) {
        // 사용자 및 채널 검증
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        // 금칙어 필터링
        String filteredContent = forbiddenWordService.filterMessage(dto.getContent());

        // 메시지 생성
        ChannelMessage message = ChannelMessage.builder()
                .content(filteredContent)
                .sender(sender)
                .channel(channel)
                .build();

        // 메시지 저장
        channelMessageRepository.save(message);


        updateLastReadAt(id, dto.getSenderId(), LocalDateTime.now());

        ChannelMessageResponseDTO channelMessageResponseDTO = ChannelMessageResponseDTO.fromEntity(message, forbiddenWordService);
        return channelMessageResponseDTO;
    }

    // 파일 전송
    public Long sendMessageWithFile(Long id, Long senderId, String content, MultipartFile file) {
        // 사용자 및 채널 검증
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        // 금칙어 필터링 적용
        String filteredContent = forbiddenWordService.filterMessage(content);

        String fileUrl = null;
        String fileType = null; // 파일 유형 변수


        // 파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            try {
                // ImageService의 uploadImage 메서드 호출
                fileUrl = imageService.uploadImage(file, "channel");

                // 파일 MIME 타입 및 확장자를 기반으로 파일 유형 결정
                String fileName = file.getOriginalFilename().toLowerCase();
                if (fileName.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                    fileType = "image";
                } else if (fileName.matches(".*\\.(pdf)$")) {
                    fileType = "pdf";
                } else {
                    fileType = "file";
                }
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류 발생", e);
            }
        }

        // 메시지 생성
        ChannelMessage message = ChannelMessage.builder()
                .content(filteredContent) // 필터링된 콘텐츠 저장
                .sender(sender)
                .channel(channel)
                .fileUrl(fileUrl) // 업로드된 파일 URL 저장
                .fileType(fileType) // 파일 유형 저장
                .build();

        // 메시지 저장
        channelMessageRepository.save(message);

        return message.getId(); // 메시지 ID 반환
    }



    // 채널 메시지 조회
    @Transactional(readOnly = true)
    public List<ChannelMessageResponseDTO> getMessages(Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        List<ChannelMessage> messages = channelMessageRepository.findAllByChannel(channel);

        return messages.stream()
                .map(message -> ChannelMessageResponseDTO.fromEntity(message, forbiddenWordService)) // 금칙어 필터링 포함
                .collect(Collectors.toList());
    }


    public long getUnreadCount(Long channelId, Long messageId) {
        ChannelMessage message = channelMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        LocalDateTime messageCreatedAt = message.getCreatedAt();

        long totalMembers = channelMemberRepository.countMembersInChannel(channelId);
        long readMembers = channelMemberRepository.countMembersReadOrBeyond(channelId, messageCreatedAt);
        return totalMembers - readMembers;
    }

    @Transactional
    public void updateLastReadAt(Long channelId, Long memberId, LocalDateTime readAtTime) {
        ChannelMember cm = channelMemberRepository.findByChannelIdAndMemberId(channelId, memberId);
        if (cm == null) {
            throw new IllegalArgumentException("ChannelMember not found");
        }

        // 새로 읽은 시점이 이전보다 최신이라면 갱신
        if (cm.getLastReadAt() == null || cm.getLastReadAt().isBefore(readAtTime)) {
            cm.changeLastReadAt(readAtTime);
        }

        
        // TODO: 방문 소켓 보내기 - 소켓 컨트롤러 안돼서 서비스에서 호출
//        messagingTemplate.convertAndSend("/topic/chatting/channel/" + channelId + "/visit", "message");
    }
}
