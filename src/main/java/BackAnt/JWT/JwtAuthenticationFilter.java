package BackAnt.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
    날짜 : 2024/11/27
    이름 : 최준혁
    내용 : JwtAuthenticationFilter 생성
*/

@Log4j2
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";

    // 로그인 또는 공개 API 경로는 필터 제외
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/public/") || uri.equals("/api/user/login")
                || uri.startsWith("/api/ws")    // WebSocket 관련
                || uri.startsWith("/ws")        // WebSocket endpoint
                || uri.startsWith("/topic")     // STOMP topic
                || uri.startsWith("/app");      // STOMP destination prefix;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 토큰 추출 (헤더 또는 쿠키에서)
        String token = resolveToken(request);
        log.info("토큰 추출 완료: {}", token);

        // 토큰이 있을 경우에만 검증
        if (token != null) {
            try {
                jwtProvider.validateToken(token); // 토큰 유효성 검증
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 정보 설정
                log.info("인증 성공: {}", authentication);
            } catch (Exception e) {
                log.info("토큰 검증 실패: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"유효하지 않은 토큰\", \"message\": \"" + e.getMessage() + "\"}");
                return;
            }
        } else {
            log.info("요청에 토큰이 없습니다.");
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // 토큰 추출 로직 (헤더와 쿠키 처리)
    private String resolveToken(HttpServletRequest request) {
        // 1. Authorization 헤더에서 Access Token 추출
        String header = request.getHeader(AUTH_HEADER);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            log.info("Authorization 헤더에서 토큰 추출 성공");
            return header.substring(TOKEN_PREFIX.length()).trim();
        }

        // 2. 쿠키에서 Refresh Token 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    log.info("쿠키에서 Refresh Token 추출 성공");
                    return cookie.getValue();
                }
            }
        }

        log.info("토큰을 찾을 수 없습니다.");
        return null; // 토큰이 없으면 null 반환
    }
}