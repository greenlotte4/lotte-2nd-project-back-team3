package BackAnt.repository.board;

import BackAnt.entity.board.BoardCategory;
import BackAnt.entity.board.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/*
   날 짜 : 2024/12/23(월)
   담당자 : 강은경
   내 용 : BoardCategory 를 위한 Repository 생성


*/
@Repository
public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Long> {


}

