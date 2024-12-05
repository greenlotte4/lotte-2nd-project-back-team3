package BackAnt.service;

import BackAnt.dto.BoardDTO;
import BackAnt.entity.Board;
import BackAnt.entity.User;
import BackAnt.repository.BoardRepository;
import BackAnt.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
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
    public Board getBoardsById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + id));
    }


    // 글쓰기 (Create)
//    public BoardDTO insertBoard(BoardDTO boardDTO) {
//        Board board = modelMapper.map(boardDTO, Board.class);
//        Board savedBoard = boardRepository.save(board);
//        return modelMapper.map(savedBoard, BoardDTO.class);
//    }

    // 글쓰기 (Create)
//    @Transactional
//    public Long insertBoard(BoardDTO boardDTO, HttpServletRequest req) {
//        // IP 주소 획득
//        String regIp = req.getRemoteAddr();
//        log.info("여기는 서비스 - 클라이언트 IP: {}", regIp);
//
//        // DTO -> Entity 변환 및 IP 설정
//        Board board = modelMapper.map(boardDTO, Board.class);
//        board.setRegIp(regIp);
//
//        // 저장
//        Board savedBoard = boardRepository.save(board);
//        return savedBoard.getId();
//    }


    // 글쓰기
//    public Long save (BoardDTO boardDTO) {
//
//        try {
//            // DTO → Entity 변환
//            Board board = modelMapper.map(boardDTO, Board.class);
//
//            User user = userRepository.findByUid("ceo001")
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            board.setWriter(user);
//
//            Board savedBoard = boardRepository.save(board);
//            log.info("게시글 저장 성공: " + savedBoard.getId());
//            return savedBoard.getId();
//        } catch (Exception e) {
//            log.error("게시글 저장 실패: " + e.getMessage());
//            throw new RuntimeException("게시글 저장에 실패했습니다", e);
//        }
//
//
//    }

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



    }
