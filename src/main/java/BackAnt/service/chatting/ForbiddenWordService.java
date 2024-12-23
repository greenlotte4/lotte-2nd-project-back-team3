package BackAnt.service.chatting;

import BackAnt.dto.chatting.ForbiddenWordDTO;
import BackAnt.entity.chatting.ForbiddenWord;
import BackAnt.repository.chatting.ForbiddenWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForbiddenWordService {

    private final ForbiddenWordRepository forbiddenWordRepository;

    // 금칙어 추가
    public ForbiddenWordDTO addForbiddenWord(ForbiddenWordDTO dto) {
        ForbiddenWord forbiddenWord = new ForbiddenWord(dto.getWord());
        forbiddenWord = forbiddenWordRepository.save(forbiddenWord);
        return new ForbiddenWordDTO(forbiddenWord.getId(), forbiddenWord.getWord());
    }

    // 금칙어 삭제
    public void deleteForbiddenWord(Long id) {
        forbiddenWordRepository.deleteById(id);
    }

    // 금칙어 조회
    public List<ForbiddenWordDTO> getAllForbiddenWords() {
        return forbiddenWordRepository.findAll()
                .stream()
                .map(word -> new ForbiddenWordDTO(word.getId(), word.getWord()))
                .collect(Collectors.toList());
    }

    // 메시지 필터링
    public String filterMessage(String message) {
        if (message == null || message.isBlank()) {
            return message;
        }

        List<ForbiddenWord> forbiddenWords = forbiddenWordRepository.findAll();
        String filteredMessage = message;

        for (ForbiddenWord word : forbiddenWords) {
            filteredMessage = filteredMessage.replaceAll("\\b" + Pattern.quote(word.getWord()) + "\\b", "***");
        }

        return filteredMessage;
    }
}
