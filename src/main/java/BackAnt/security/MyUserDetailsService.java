package BackAnt.security;


import BackAnt.entity.User;
import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*
    날짜 : 2024/11/27
    이름 : 최준혁
    내용 : MyUserDetailsService 생성
*/

@Log4j2
@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 사용자가 입력한 아이디로 사용자 조회, 비밀번호에 대한 검증은 이전 컴포넌트인 AuthenticationProvider에서 수행
        Optional<User> optUser = userRepository.findByUid(username);
        log.info("dsafasdf" );
        if(optUser.isPresent()) {
            // 시큐리티 사용자 인증객체 생성 후 반환
            MyUserDetails myUserDetails = MyUserDetails.builder()
                    .user(optUser.get())
                    .build();
            return myUserDetails;
        }

        // 사용자가 입력한 아이디가 없을 경우
        return null;
    }
}