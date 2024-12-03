package BackAnt.controller.user;

import BackAnt.dto.RequestDTO.AdminRequestDTO;
import BackAnt.dto.RequestDTO.LoginRequestDTO;
import BackAnt.dto.ResponseDTO.ApiResponseDTO;
import BackAnt.entity.User;
import BackAnt.security.MyUserDetails;
import BackAnt.service.AuthService;
import BackAnt.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    // 초기 관리자 멤버 추가
    @PostMapping("/create")
    public ResponseEntity<?> addUser(@RequestBody AdminRequestDTO adminDTO) {
        try {
            log.info("유저 " + adminDTO.toString());
            User savedUser = userService.createUser(adminDTO);
            // 성공 응답
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "사용자 저장 성공", savedUser.getId()));
        } catch (Exception e) {
            // 실패 응답
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.fail("사용자 저장 실패: " + e.getMessage()));
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        try {
            log.info("로그인들어오나");
            System.out.println("로그인??");
            // 로그인 처리
            String accessToken = authService.login(loginRequest.getUid(), loginRequest.getPassword());

            // 리프레시 토큰 생성
            String refreshToken = authService.refreshToken(accessToken);

            // 리프레시 토큰을 쿠키로 저장
            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 유효
            response.addCookie(refreshCookie);

            log.info("Access Token: {}", accessToken);
            log.info("Refresh Token: {}", refreshToken);


            // 응답 반환
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "로그인 성공", accessToken));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new ApiResponseDTO<>(false, "로그인 실패: " + e.getMessage(), null));
        }
    }

    // 리프레시 토큰 검증
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue("refreshToken") String refreshToken) {
        try {
            log.info("리프레시들어오나");
            // 토큰 갱신
            String newAccessToken = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "토큰 갱신 성공", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new ApiResponseDTO<>(false, "토큰 갱신 실패: " + e.getMessage(), null));
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            log.info("로그아웃 요청");

            // 리프레시 토큰 쿠키 삭제
            Cookie deleteCookie = new Cookie("refreshToken", null);
            deleteCookie.setHttpOnly(true);
            deleteCookie.setSecure(true);
            deleteCookie.setPath("/");
            deleteCookie.setMaxAge(0); // 쿠키 삭제
            response.addCookie(deleteCookie);

            return ResponseEntity.ok(new ApiResponseDTO<>(true, "로그아웃 성공", null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponseDTO<>(false, "로그아웃 실패: " + e.getMessage(), null));
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자가 인증되지 않았습니다.");
        }

        User user = userDetails.getUser();
        return ResponseEntity.ok(user);
    }

}
