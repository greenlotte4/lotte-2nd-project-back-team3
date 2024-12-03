package BackAnt.service;

import BackAnt.dto.BoardDTO;
import BackAnt.entity.Board;
import BackAnt.entity.User;
import BackAnt.repository.BoardRepository;
import BackAnt.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*
    날 짜 : 2024/12/02(월)
    담당자 : 김민희
    내 용 : Board 를 위한 Service 생성
*/

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public int save (BoardDTO boardDTO) {

        // DTO → Entity 변환
        Board board = modelMapper.map(boardDTO, Board.class);

        // 정적으로 작성자를 설정
        User user = new User(); // User 엔티티 생성
        user.setId(2L);          // 작성자 ID를 하드코딩
        user.setName("Test User"); // 작성자 이름도 하드코딩
        board.setWriter(user);  // 작성자 연결

        log.info("하드코딩된 작성자: " + user);
        log.info("보드: " + board);

        // 저장
        Board savedBoard = boardRepository.save(board);
        // 저장된 게시글 번호 반환
        return savedBoard.getNo();

    }


}
