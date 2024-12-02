package BackAnt.repository;

import BackAnt.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/*
    날짜 : 2024/11/29
    이름 : 최준혁
    내용 : CompanyRepository 생성
*/
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}