package BackAnt.service;

import BackAnt.JWT.JwtProvider;
import BackAnt.entity.User;
import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    // 로그인 처리
    public String login(String uid, String password) {
        // 사용자 조회
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        // 계정 상태 확인
        if (user.getStatus() != BackAnt.entity.enums.Status.ACTIVE) {
            throw new IllegalArgumentException("계정이 활성화되어 있지 않습니다.");
        }
        // 액세스 토큰 생성
        return jwtProvider.createToken(user, 15); // 유효 시간 15분
    }

    // 리프레시 토큰
    public String refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        jwtProvider.validateToken(refreshToken);

        // 아이디 추출
        String uid = jwtProvider.getClaims(refreshToken).get("uid", String.class);

        // 사용자 조회
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("리프레시 사용자를 찾을 수 없습니다."));

        // 새로운 액세스 토큰 생성
        return jwtProvider.createToken(user, 15); // 유효 시간 15분
    }
}
