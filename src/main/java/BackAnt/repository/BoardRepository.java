package BackAnt.repository;

import BackAnt.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
    날 짜 : 2024/12/02(월)
    담당자 : 김민희
    내 용 : Board 를 위한 Repository 생성 (MongoDB 로 전환 예정)
*/

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {


}
