package BackAnt.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청주소에서 마지막 문자열 추출
        //String uri = request.getRequestURI();
        //int i = uri.lastIndexOf("/");
        //String path = uri.substring(i);
        //log.info("here1 - " + path);

        // 토큰 추출
        String header = request.getHeader(AUTH_HEADER);
        log.info("here2 - " + header);

        String token = null;
        if(header != null && header.startsWith(TOKEN_PREFIX)) {
            token = header.substring(TOKEN_PREFIX.length()).trim();
        }
        log.info("here3 - " + token);

        // 토큰 검사
        if(token != null) {

            try {
                jwtProvider.validateToken(token);

                // 토큰이 이상없으면 시큐리티 인증 처리
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("here4 - " + authentication);

            } catch (Exception e) {
                // 토큰이 이상이 있으면 실패 응답
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(e.getMessage());
                log.info("here5 - " + e.getMessage());
                return; // 처리종료
            }
        }

        log.info("here6...");
        filterChain.doFilter(request, response);
    }
}