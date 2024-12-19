package BackAnt.service.board;

import BackAnt.dto.board.comment.BoardCommentRequestDTO;
import BackAnt.dto.board.comment.BoardCommentUpdateDTO;
import BackAnt.repository.UserRepository;
import BackAnt.repository.board.BoardCommentRepository;
import BackAnt.repository.board.BoardRepository;
import BackAnt.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import BackAnt.dto.board.comment.BoardCommentDTO;
import BackAnt.entity.User;
import BackAnt.entity.board.Board;
import BackAnt.entity.board.BoardComment;
//import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;


    // ***** 댓글 작성 *****
    @Transactional
    public BoardCommentDTO createBoardComment
    (Long boardId, Long userId, BoardCommentRequestDTO dto, HttpServletRequest request) {
        log.info("123123123123123");
        log.info("댓글 작성 시작 - 게시글 번호: {}, 작성자 ID: {}", boardId, userId);

        // 게시글 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 작성자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 댓글 생성
        BoardComment comment = BoardComment.builder()
                .board(board)
                .commentWriter(user)
                .commentContent(dto.getContent())
                .secret(dto.getSecret())
                .regIp(request.getRemoteAddr())
                .build();

//        // 부모 댓글 확인
//        if (dto.getParentCommentId() != null) {
//            BoardComment parentComment = boardCommentRepository.findById(dto.getParentCommentId())
//                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
//            comment.setParentComment(parentComment);
//        }

        // 댓글 저장
        BoardComment savedComment = boardCommentRepository.save(comment);

        BoardCommentDTO commentDTO = convertToDTO(savedComment);

        // DTO로 변환 후 반환
        return commentDTO;
    }



    // 댓글 수정
    @Transactional
    public BoardCommentDTO updateBoardComment(Long commentId, Long userId, BoardCommentUpdateDTO dto) {
        log.info("(서비스) 댓글 수정 시작 - 댓글 번호: {}, 수정자 ID: {}", commentId, userId);

        // 댓글 확인
        BoardComment comment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!comment.getCommentWriter().getId().equals(userId)) {
            throw new IllegalStateException("댓글 수정 권한이 없습니다.");
        }

        // 댓글 내용 수정 (save 역할)
        comment.updateComment(dto.getContent(), dto.getSecret());
        log.info("(서비스) 댓글 수정 완료 - 댓글 번호: {}", commentId);

        // 수정된 댓글 반환
        return modelMapper.map(comment, BoardCommentDTO.class);
    }



    // 댓글 삭제
    @Transactional
    public void deleteBoardComment(Long commentId, Long userId) {
        log.info("(서비스) 댓글 삭제 시작 - 댓글 번호: {}, 삭제자 ID: {}", commentId, userId);

        // 댓글 확인
        BoardComment comment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("(서비스) 댓글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!comment.getCommentWriter().getId().equals(userId)) {
            throw new IllegalStateException("(서비스) 댓글 삭제 권한이 없습니다.");
        }

        // 댓글 삭제
        boardCommentRepository.delete(comment);
        log.info("(서비스) 댓글 삭제 완료 - 댓글 번호: {}", commentId);
    }



    // 게시글의 모든 댓글 조회
    public List<BoardCommentDTO> getBoardCommentList(Long boardId, Long userId) {
        log.info("(서비스) 댓글 목록 조회 - 게시글 번호: {}, 요청자 ID: {}", boardId, userId);

        // 게시글에 해당하는 댓글 조회
        List<BoardComment> comments = boardCommentRepository.findByBoardId(boardId);
        log.info("(서비스) 댓글 조회? " + comments.toString());
        return comments.stream()
                .map(comment -> {

                    BoardCommentDTO dto = convertToDTO(comment);

                    // 비밀 댓글 처리: 작성자 또는 관리자만 내용 확인 가능
                    if (comment.getSecret() &&
                            !comment.getCommentWriter().getId().equals(userId)) {
                        dto.setContent("비밀 댓글입니다.");
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }



    private BoardCommentDTO convertToDTO(BoardComment comment) {
        return BoardCommentDTO.builder()
                .id(comment.getId())
                .content(comment.getCommentContent())
                .writerName(comment.getCommentWriter().getName())
                .writerDepartment(comment.getCommentWriter().getDepartment().getName())
                .writerImage(comment.getCommentWriter().getProfileImageUrl())
                .secret(comment.getSecret())
                .createdAt(comment.getCreatedAt().toString())
                .updatedAt(comment.getUpdatedAt())
                .boardId(comment.getBoard().getId())
                .writerId(comment.getCommentWriter().getId())
//                .parentCommentId(comment.getParentComment() != null ?
//                        comment.getParentComment().getCommentId() : null)
                .build();
    }


}
