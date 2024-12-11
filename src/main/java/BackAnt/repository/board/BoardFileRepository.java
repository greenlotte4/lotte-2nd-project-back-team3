package BackAnt.repository.board;

import BackAnt.entity.board.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
    날 짜 : 2024/12/10(화)
    담당자 : 김민희
    내 용 : Board File 를 위한 Repository 생성
*/

@Repository
public interface BoardFileRepository extends JpaRepository<BoardFile, Integer> {


    List<BoardFile> findByBoardId(long boardId);

}
