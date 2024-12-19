package BackAnt.repository;

import BackAnt.entity.Company;
import BackAnt.entity.User;
import BackAnt.entity.chatting.Channel;
import BackAnt.entity.chatting.ChannelMessage;
import BackAnt.entity.chatting.Dm;
import BackAnt.entity.chatting.DmMember;
import BackAnt.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/*
    날짜 : 2024/11/29
    이름 : 최준혁
    내용 : UserRepository 생성
*/
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 아이디로 사용자 조회
    Optional<User> findByUid(String uid);

    Optional<User> findById(Long id);

    // status가 ACTIVE인 사용자 조회
    List<User> findByStatus(Status status);

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 아이디 중복 확인
    boolean existsByUid(String uid);

    // 회사별 유저 조회 (페이징 처리)
    Page<User> findAllByCompanyAndStatus(Company company, Status status, Pageable pageable);

    // 부서별 유저 조회
    List<User> findByDepartmentId(Long departmentId);

    List<User> findAllByCompany(Company company);

    // 회사 ID로 사용자 조회
    List<User> findByCompanyId(Long companyId);

    // 회사 ID로 사용자 조회 (+ 페이징)
    Page<User> findByCompanyId(Long companyId, Pageable pageable);

    // 회사 대표이사 조회
    List<User> findByCompanyAndPosition(Company company, String position);

    // 유저 아이디 조회
    User findByNameAndEmail(String name, String email);
}
