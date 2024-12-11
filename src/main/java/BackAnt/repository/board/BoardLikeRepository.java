package BackAnt.repository.board;

import BackAnt.entity.board.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
    날 짜 : 2024/12/05(금)
    담당자 : 김민희
    내 용 : Board 를 위한 Repository 생성
           - 게시글 좋아요 기능을 위한 데이터 접근 인터페이스
*/

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Integer> {
    // 게시글에 대한 사용자의 좋아요 여부 확인
    boolean existsByBoardIdAndUserId(Long boardId, Long uid);
    // 게시글에 대한 사용자의 좋아요 삭제
    void deleteByBoardIdAndUserId(Long boardId, Long uid);
    int countByBoardId(Long boardId);

}
