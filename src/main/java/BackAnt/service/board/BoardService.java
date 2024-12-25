package BackAnt.service.board;

import BackAnt.JWT.JwtProvider;
import BackAnt.dto.board.*;
import BackAnt.entity.board.Board;
import BackAnt.entity.User;
import BackAnt.entity.board.BoardLike;
import BackAnt.repository.board.BoardCategoryRepository;
import BackAnt.repository.board.BoardCommentRepository;
import BackAnt.repository.board.BoardLikeRepository;
import BackAnt.repository.board.BoardRepository;
import BackAnt.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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
    private final BoardCategoryService boardCategoryService;
    private final BoardCategoryRepository boardCategoryRepository;
    private final BoardCommentRepository boardCommentRepository;

    // 글 목록 조회
    public Page<BoardDTO> getFindAllBoards(Pageable pageable) {
        Page<Board> boards = boardRepository.findAllByOrderByRegDateDesc(pageable);

        // Board -> BoardDTO 변환 시 상세 로그 추가
        return boards.map(board -> {
            BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
            boardDTO.setWriterId(board.getWriter() != null ? board.getWriter().getId() : null);
            boardDTO.setWriterName(board.getWriter() != null ? board.getWriter().getName() : "익명");

            int likeCount = boardLikeRepository.countByBoardId(board.getId());
            boardDTO.setLikes(likeCount);

            log.info("(서비스) 변환된 DTO: " + boardDTO);
            return boardDTO;
        });
    }

    // 글 검색
//    public Page<BoardDTO> searchBoards(
//            String keyword, Pageable pageable) {
//        log.info("(서비스) 글검색 시작");
//        Page<Board> boardPage = boardRepository.searchByKeyword(keyword, pageable);
//        log.info("(서비스) 글검색 boardPage : "+ boardPage);
//
//        return boardPage.map(board -> {
//            BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
//            log.info("boardDTO: " + boardDTO);
//            boardDTO.setWriterName(board.getWriter() != null ? board.getWriter().getName() : "익명");
//            return boardDTO;
//        });
//    }

    public Page<BoardSearchDTO> searchBoards(String type, String keyword, Pageable pageable) {
        log.info("(서비스) 검색어: " + keyword);


        if(Objects.equals(type, "title")){
            Page<Board> boardPage = boardRepository.searchByTitle(keyword, pageable);
            log.info("1111::"+boardPage);
            return boardPage.map(board -> modelMapper.map(board, BoardSearchDTO.class));
        }else if(Objects.equals(type, "content")){
            Page<Board> boardPage = boardRepository.searchByContent(keyword, pageable);
            log.info("2222::"+boardPage);
            return boardPage.map(board -> modelMapper.map(board, BoardSearchDTO.class));
        }else if(Objects.equals(type, "writerName")){
            Page<Board> boardPage = boardRepository.searchByWriterName(keyword, pageable);
            log.info("3333::"+boardPage);
            return boardPage.map(board -> modelMapper.map(board, BoardSearchDTO.class));
        }

        Page<Board> boardPage = boardRepository.searchByKeyword(keyword, pageable);
        log.info("(서비스) 검색된 게시글 수: " + boardPage.getTotalElements());

        // ModelMapper를 활용해 Board -> SearchDTO 변환
        return null;
    }



    // 글 상세 조회
    public BoardResponseViewDTO getBoardsById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException
                ("해당 게시글을 찾을 수 없습니다. (게시글 번호: " + id + ")"));


        log.info("board : "+board);

        // 조회수 증가
        board.setHit(board.getHit() + 1);
        boardRepository.save(board);

        // 기본 매핑
        BoardResponseViewDTO dto = modelMapper.map(board, BoardResponseViewDTO.class);
        dto.setWriter(""+board.getWriter().getId());
        dto.setWriterName(board.getWriter().getName());

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

    // 댓글 카운트
    public int getCommentCount(Long boardId) {
        return boardCommentRepository.countCommentsByBoardId(boardId);
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

            // boardDTO에 담긴 FK boardCategoryId로 boardCategory 조회 후 SET
            board.setCategory(boardCategoryRepository.findById(boardDTO.getCategoryId()).orElse(null));

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
        log.info("글 수정 서비스 시작: id={}", id);
        log.info("폼 데이터 + boardDTO: {}", boardDTO.toString());
        log.info("게시글 아이디 id={}", boardDTO.getId());

        // 1. 게시글 조회
        Board board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 2. 수정 권한 확인
        if (!board.getWriter().getId().equals(id)) {
            throw new AccessDeniedException("게시글 수정 권한이 없습니다.");
        }

        // 3. 게시글 수정
        if (boardDTO.getTitle() != null) {
            board.setTitle(boardDTO.getTitle());
        }
        if (boardDTO.getContent() != null) {
            board.setContent(boardDTO.getContent());
        }

        // 4. 수정일시 업데이트
        board.setUpdateDate(LocalDateTime.now());

        // 5. 저장
        Board savedBoard = boardRepository.save(board);
        log.info("게시글 수정 완료: id={}", id);

        // 6. 좋아요 수 구하기
        int likes = boardLikeRepository.countByBoardId(savedBoard.getId());

        // 7. DTO 변환 및 반환
        return BoardDTO.of(savedBoard, likes);  // likes 를 함께 전달
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
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        boardRepository.delete(board);
        log.info("🗑️ 게시글 삭제 완료 - 게시글 ID: {}", id);
    }

    // 카테고리별 게시글 조회
    public List<BoardDTO> getBoardsByCategory(Long categoryId){
        List<Board> boards = boardRepository.findByCategoryId(categoryId);

        return boards.stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
            .collect(Collectors.toList());
    }


}
