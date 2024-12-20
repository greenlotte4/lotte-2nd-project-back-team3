package BackAnt.service.landing;


import BackAnt.dto.landing.QnaRequestDTO;
import BackAnt.dto.landing.QnaResponseDTO;
import BackAnt.entity.landing.Qna;
import BackAnt.exception.CustomApplicationException;
import BackAnt.repository.landing.QnaRepository;
import BackAnt.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class QnaService {
    private final QnaRepository qnaRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public QnaResponseDTO insertQna(QnaRequestDTO dto) {

        try {
            dto.setTempPassword(passwordEncoder.encode(dto.getTempPassword())); // 비밀번호 암호화
            Qna savedQna = qnaRepository.save(modelMapper.map(dto, Qna.class));

            return modelMapper.map(savedQna, QnaResponseDTO.class);

        } catch (DataAccessException e) {
            // 데이터 접근 관련 예외 처리
            throw new CustomApplicationException("데이터베이스 처리 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            // 일반적인 예외 처리
            throw new CustomApplicationException("문의 등록 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }
    public List<QnaResponseDTO>selectListByEmail(String email, String tempPassword) {

        List<Qna> qnas = qnaRepository.findByEmail(email);
        if(qnas.isEmpty()){
            return null;
        }
        // 비밀번호가 일치하는 문의 내역만 필터링
        List<Qna> matchedQnas = qnas.stream()
                .filter(qna -> passwordEncoder.matches(tempPassword, qna.getTempPassword()))
                .toList();

        return matchedQnas.stream()
                .map(qna -> modelMapper.map(qna, QnaResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<QnaResponseDTO> selectAllForAdmin(){
        List<Qna> qnas = qnaRepository.findAll();

        if(qnas.isEmpty()){
            return null;
        }

        return qnas.stream()
                .map(qna->modelMapper.map(qna, QnaResponseDTO.class))
                .toList();
    }

    public QnaResponseDTO updateAnswer(Long id, String answer) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다."));
        qna.setAnswer(answer);
        qna.setAnsweredAt(LocalDateTime.now());
        Qna savedQna = qnaRepository.save(qna);

        return modelMapper.map(savedQna, QnaResponseDTO.class);
    }

    public QnaResponseDTO modifyQna(Long id, QnaRequestDTO requestDTO) {

        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다."));

        Qna savingQna  = modelMapper.map(requestDTO, Qna.class);
        savingQna.setId(id);

        Qna savedQna = qnaRepository.save(savingQna);

        return modelMapper.map(savedQna, QnaResponseDTO.class);
    }
}
