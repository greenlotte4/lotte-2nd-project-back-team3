package BackAnt.repository.board;

import BackAnt.entity.board.BoardComment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    List<BoardComment> findByBoardId(Long boardId);

//
//    // 특정 게시글의 모든 최상위 댓글 조회 (대댓글 제외)
//    @Query("SELECT c FROM BoardComment c WHERE c.board.id = :boardId AND c.parentComment IS NULL ORDER BY c.createdAt ASC")
//    List<BoardComment> findTopLevelCommentsByBoardId(@Param("boardId") Long boardId);
//
//    // 특정 댓글의 모든 대댓글 조회
//    List<BoardComment> findByParentCommentIdOrderByCreatedAtAsc(Long parentId);
//
//    // 특정 사용자가 작성한 모든 댓글 조회
//    List<BoardComment> findByCommentWriter_IdOrderByCreatedAtDesc(Long userId);

    // 댓글 카운트
    @Query("SELECT COUNT(c) FROM BoardComment c WHERE c.board.id = :boardId")
    int countCommentsByBoardId(@Param("boardId") Long boardId);


}

