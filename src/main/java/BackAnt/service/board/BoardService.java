package BackAnt.service.board;

import BackAnt.JWT.JwtProvider;
import BackAnt.dto.board.BoardDTO;
import BackAnt.dto.board.BoardFileDTO;
import BackAnt.dto.board.BoardResponseViewDTO;
import BackAnt.entity.board.Board;
import BackAnt.entity.User;
import BackAnt.entity.board.BoardLike;
import BackAnt.repository.board.BoardLikeRepository;
import BackAnt.repository.board.BoardRepository;
import BackAnt.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;



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
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;
    private final BoardFileService boardFileService;


    // 글 목록 조회
    public Page<Board> getFindAllBoards(Pageable pageable) {
        return boardRepository.findAllByOrderByRegDateDesc(pageable);
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
        BoardResponseViewDTO dto = modelMapper.map(board, BoardResponseViewDTO.class);
        dto.setWriter(""+board.getWriter().getId());
        return dto;
    }

    // 글 상세 조회 - (좋아요 기능)
    public boolean toggleLike(Long boardId) {
        // Jwt 에서 사용자 정보 추출
        String jwt = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        Claims claims = jwtProvider.getClaims(jwt);
        String uid = claims.get("uid", String.class);

        log.info("좋아요 처리 시작 - 게시글: {}, 사용자: {}", boardId, uid);

        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없음 - uid: {}", uid);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> {
                    log.error("게시글을 찾을 수 없음 - 게시글 번호: {}", boardId);
                    return new IllegalArgumentException("게시글이 존재하지 않습니다.");
                });

        boolean exists = boardLikeRepository.existsByBoardIdAndUserId(boardId, user.getId());

        if (exists) {
            log.info("좋아요 취소 진행 - 게시글: {}, 사용자: {}", boardId, uid);
            boardLikeRepository.deleteByBoardIdAndUserId(boardId, user.getId());
            board.setLikes(board.getLikes() - 1);
        } else {
            log.info("좋아요 추가 진행 - 게시글: {}, 사용자: {}", boardId, uid);
            BoardLike boardLike = BoardLike.builder()
                    .boardId(boardId)
                    .user(user)
                    .nick(user.getName())
                    .build();
            boardLikeRepository.save(boardLike);
            board.setLikes(board.getLikes() + 1);
        }

        boardRepository.save(board);
        log.info("좋아요 처리 완료 - 게시글: {}, 사용자: {}, 결과: {}",
                boardId, uid, exists ? "취소" : "추가");

        return !exists;
    }

    // 좋아요 수 반환
    public int getLikes(Long boardId) {
        // 게시글 존재 여부 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 해당 게시글의 좋아요 수 반환
        return boardLikeRepository.countByBoardId(boardId);
    }

    // 글 쓰기
    @Transactional
    public Long save(BoardDTO boardDTO, HttpServletRequest req) {
        log.info("안녕하시렵니가? 글쓰기 서비스 입니다...");
        try {
            // DTO → Entity 변환
            Board board = modelMapper.map(boardDTO, Board.class);
            board.setRegIp(req.getRemoteAddr()); // 클라이언트 IP 주소 저장

            // 작성자 정보 DB 조회
            User user = userRepository.findById(boardDTO.getWriterId())
                    .orElseThrow(() -> new RuntimeException("글쓰기 사용자를 찾을 수 없습니다."));


            board.setWriter(user);
            log.info("글쓰기 서비스 board 작성자 ID: {}", user.getId());

            // 게시글 DB 저장
            Board savedBoard = boardRepository.save(board);
            log.info("게시글 저장 성공 (글쓰기 성공 -!) : {}", savedBoard.getId());

            // 저장된 게시글 ID 반환
            return savedBoard.getId();

        } catch (Exception e) {
            log.error("게시글 저장 실패: {}", e.getMessage());
            throw new RuntimeException("게시글 저장에 실패했습니다", e);
        }
    }

    // 글 수정
    @Transactional
    public BoardDTO updateBoard(Long id, BoardDTO boardDTO) {
        log.info("글 수정 서비스");


        // 현재 사용자 정보 가져오기
        String uid = getCurrentUserUid();
        log.info("사용자 정보"+id);

        // 게시글 조회 및 권한 확인
        Board board = validateAndGetBoard(id, uid);
        log.info("게시글 조회 및 권한 확인"+board);

        // DTO -> Entity 매핑 (변경된 필드만)
        modelMapper.map(boardDTO, board);
        log.info("변경된 필드만"+boardDTO);


        // 저장 및 DTO 변환하여 반환
        Board savedBoard = boardRepository.save(board);
        log.info("변경된 필드만"+savedBoard);
        return modelMapper.map(savedBoard, BoardDTO.class);

    }

    // 현재 사용자 UID 조회
    private String getCurrentUserUid() {
        String jwt = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        Claims claims = jwtProvider.getClaims(jwt);
        return claims.get("uid", String.class);
    }

    // 게시글 조회 및 권한 검증
    private Board validateAndGetBoard(Long id, String uid) {
        // 사용자 확인
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 게시글 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 권한 확인
        if (!board.getWriter().equals(user.getUid())) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }

        return board;
    }


    // 글 삭제
    public void deleteBoard(Long id) {
        log.info("🗑️ 게시글 삭제 id: {}", id);
        boardRepository.deleteById(id);
    }


}
