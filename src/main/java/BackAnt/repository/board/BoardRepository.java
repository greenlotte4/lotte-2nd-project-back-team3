package BackAnt.repository.board;

import BackAnt.dto.board.BoardDTO;
import BackAnt.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
    날 짜 : 2024/12/02(월)
    담당자 : 김민희
    내 용 : Board 를 위한 Repository 생성 (MongoDB 로 전환 예정)
*/

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 게시글과 작성자, 회사 정보를 한 번에 조회
    @Query("SELECT b FROM Board b JOIN FETCH b.writer w JOIN FETCH w.company")
    List<Board> findAllWithWriterAndCompany();

    // 게시글 + 작성자(user) 정보 조회
    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.writer w " +
            "ORDER BY b.regDate DESC")  // 최신글 순으로 정렬
    List<Board> findAllWithWriter();

    // 글 목록 전체 조회
    @Query("SELECT NEW BackAnt.dto.board.BoardResponseViewDTO(b.id, b.cate1, b.cate2, b.title, " +
            "b.comment, b.content, b.writer.name, b.file, b.hit, b.likes, " +
            "b.regIp, b.regDate) " +
            "FROM Board b " +
            "JOIN b.writer w " +
            "ORDER BY b.regDate DESC")
    List<BoardDTO> findAllBoardDTOs();




}