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
public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findByUid(String uid);
}
