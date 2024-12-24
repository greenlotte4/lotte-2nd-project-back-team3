package BackAnt.repository.board;

import BackAnt.dto.board.BoardDTO;
import BackAnt.entity.board.Board;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
    @Query("SELECT NEW BackAnt.dto.board.BoardResponseViewDTO(b.id, b.title, " +
            "b.comment, b.content, b.writer.name, b.file, b.hit, b.likes, " +
            "b.regIp, b.regDate) " +
            "FROM Board b " +
            "JOIN b.writer w " +
            "ORDER BY b.regDate DESC")
    List<BoardDTO> findAllBoardDTOs();

    // 최신 글 순으로 정렬
    Page<Board> findAllByOrderByRegDateDesc(Pageable pageable);

    // 특정 카테고리에 속한 게시글 조회 - 2024/12/23 강은경
    @Query("SELECT b FROM Board b WHERE b.category.id = :categoryId")
    List<Board> findByCategoryId(Long categoryId);

    // 카테고리 id로 게시글 삭제 - 2024/12/23 강은경
    @Modifying
    @Transactional
    @Query("DELETE FROM Board b WHERE b.category.id = :categoryId")
    void deleteByCategoryId(Long categoryId);

    // **검색 기능** - 제목, 작성자 이름, 내용에서 키워드 검색
    @Query("SELECT b FROM Board b " +
            "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.writer.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Board> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Board> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "WHERE LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Board> searchByContent(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "WHERE LOWER(b.writer.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Board> searchByWriterName(@Param("keyword") String keyword, Pageable pageable);

}
