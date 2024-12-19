package BackAnt.service.chatting;

import BackAnt.dto.chatting.ChannelMessageCreateDTO;
import BackAnt.dto.chatting.ChannelMessageResponseDTO;
import BackAnt.entity.User;
import BackAnt.entity.chatting.Channel;
import BackAnt.entity.chatting.ChannelMessage;
import BackAnt.repository.UserRepository;
import BackAnt.repository.chatting.ChannelMessageRepository;
import BackAnt.repository.chatting.ChannelRepository;
import BackAnt.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelMessageService {
    private final ChannelMessageRepository channelMessageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ImageService imageService;

    // 채널 메시지 보내기
    public Long sendMessage(Long id, ChannelMessageCreateDTO dto) {
        // 사용자 및 채널 검증
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        // 메시지 생성
        ChannelMessage message = ChannelMessage.builder()
                .content(dto.getContent())
                .sender(sender)
                .channel(channel)
                .build();

        // 메시지 저장
        channelMessageRepository.save(message);

        return message.getId(); // 메시지 ID 반환
    }

    // 파일 전송
    public Long sendMessageWithFile(Long id, Long senderId, String content, MultipartFile file) {
        // 사용자 및 채널 검증
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

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
                .content(content)
                .sender(sender)
                .channel(channel)
                .fileUrl(fileUrl) // 업로드된 파일 URL 저장
                .fileType(fileType) // 파일 유형 저장
                .build();

        // 메시지 저장
        channelMessageRepository.save(message);

        return message.getId(); // 메시지 ID 반환
    }


    // 키워드로 채널 메시지 검색
    public List<ChannelMessageResponseDTO> getsearchChannelMessages(Long channelId, String keyword) {
        List<ChannelMessage> messages = channelMessageRepository.findMessagesByKeywordAndChannel(channelId, keyword);

        if (messages == null) {
            messages = new ArrayList<>();
        }

        return messages.stream()
                .map(message -> {
                    Long unreadCount = getUnreadCountForMessage(message);  // 읽지 않은 사람 수 계산
                    return ChannelMessageResponseDTO.fromEntity(message, unreadCount);  // unreadCount와 함께 DTO로 변환
                })
                .collect(Collectors.toList());
    }



    // 채널 메시지 조회
    @Transactional(readOnly = true)
    public List<ChannelMessageResponseDTO> getMessages(Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        List<ChannelMessage> messages = channelMessageRepository.findAllByChannel(channel);

        return messages.stream()
                .map(message -> {
                    Long unreadCount = getUnreadCountForMessage(message);  // 읽지 않은 사람 수 계산
                    return ChannelMessageResponseDTO.fromEntity(message, unreadCount);  // unreadCount와 함께 DTO로 변환
                })
                .collect(Collectors.toList());
    }

    // 채널 메시지 읽음 처리
    @Transactional
    public void markMessagesAsRead(Long channelId, Long userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        List<ChannelMessage> messages = channelMessageRepository.findAllByChannel(channel);

        // 메시지를 읽음 처리
        for (ChannelMessage message : messages) {
            // 본인이 보낸 메시지는 제외하고, 읽지 않은 메시지를 읽음 상태로 처리
            if (!message.getSender().getId().equals(userId) && !message.getIsRead()) {
                message.markAsRead();  // 읽음 상태로 설정
            }
        }
    }
    // 채널 메시지 읽지 않은 메시지 수 조회
    @Transactional(readOnly = true)
    public List<ChannelMessageResponseDTO> getMessagesWithUnreadCount(Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        List<ChannelMessage> messages = channelMessageRepository.findAllByChannel(channel);

        // 메시지 리스트에 읽지 않은 사람 수를 포함해서 DTO로 변환
        return messages.stream()
                .map(message -> {
                    Long unreadCount = getUnreadCountForMessage(message);  // 각 메시지에 대해 읽지 않은 사람 수 계산
                    return ChannelMessageResponseDTO.fromEntity(message, unreadCount);  // unreadCount와 함께 DTO로 변환
                })
                .collect(Collectors.toList());
    }

    private Long getUnreadCountForMessage(ChannelMessage message) {
        // 메시지 ID를 넘겨줘야 하므로 message.getId()를 사용합니다.
        return channelMessageRepository.countByIsReadFalseAndChannelAndMessage(message.getChannel(), message.getId());
    }


}
