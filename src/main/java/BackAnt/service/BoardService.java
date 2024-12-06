package BackAnt.service;

import BackAnt.dto.board.BoardDTO;
import BackAnt.dto.board.BoardResponseViewDTO;
import BackAnt.entity.board.Board;
import BackAnt.entity.User;
import BackAnt.repository.BoardRepository;
import BackAnt.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import java.util.List;

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

    // 글 목록 조회
    public List<BoardDTO> getFindAllBoards() {
        try {
            log.info("게시글 목록 조회 시작 -------------");
            List<Board> boards = boardRepository.findAllWithWriter();
            return boards.stream()
                    .map(board -> modelMapper.map(board, BoardDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생: ", e);
            throw new RuntimeException("게시글 목록을 가져오는데 실패했습니다.", e);
        }
    }


    // 글 상세 조회
    public BoardResponseViewDTO getBoardsById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException
                ("해당 게시글을 찾을 수 없습니다. (게시글 번호: " + id + ")"));

        // 조회수 증가
        board.setHit(board.getHit() + 1);
        boardRepository.save(board);

        // 기본 매핑
        return modelMapper.map(board, BoardResponseViewDTO.class);
    }

    // 글쓰기
    @Transactional
    public Long save(BoardDTO boardDTO, HttpServletRequest req) {
        try {
            // DTO → Entity 변환
            Board board = modelMapper.map(boardDTO, Board.class);

            // IP 설정
            board.setRegIp(req.getRemoteAddr());

            // 임시 사용자 설정 (실제로는 로그인된 사용자 정보를 사용해야 함)
            User user = userRepository.findByUid("qwer123")
                    .orElseThrow(() -> new RuntimeException("User not found"));
            board.setWriter(user);

            Board savedBoard = boardRepository.save(board);
            log.info("게시글 저장 성공: {}", savedBoard.getId());
            return savedBoard.getId();

        } catch (Exception e) {
            log.error("게시글 저장 실패: {}", e.getMessage());
            throw new RuntimeException("게시글 저장에 실패했습니다", e);
        }
    }

    // 글수정
    public void updateBoard(Long id, BoardDTO boardDTO) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // DTO 데이터를 엔티티로 매핑
        modelMapper.map(boardDTO, board);
        boardRepository.save(board); // 변경된 엔티티 저장
    }


    }
