package BackAnt.repository;

import BackAnt.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
    날짜 : 2024/12/8(일)
    이름 : 최준혁
    내용 : PopupRepository 생성
*/
@Repository
public interface PopupRepository extends JpaRepository<Popup, Long> {
    List<Popup> findByCompanyId(Long companyId); // 회사별 팝업 조회
}