package BackAnt.repository;

import BackAnt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

}
