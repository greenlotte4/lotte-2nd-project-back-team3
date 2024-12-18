package BackAnt.JWT;

import BackAnt.entity.User;
import BackAnt.entity.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
    날짜 : 2024/11/27
    이름 : 최준혁
    내용 : JwtProvider 생성
*/

@Getter
@Component
public class JwtProvider {

    private final String issuer;
    private final SecretKey secretKey;

    public JwtProvider(@Value("${jwt.issuer}") String issuer,
                       @Value("${jwt.secret}") String secretKey) {
        this.issuer = issuer;
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // JWT 토큰 생성
    public String createToken(User user, int days) {
        Date issuedDate = new Date();
        Date expireDate = new Date(issuedDate.getTime() + Duration.ofDays(days).toMillis());

        return Jwts.builder()
                .setIssuer(issuer)
                .setIssuedAt(issuedDate)
                .setExpiration(expireDate)
                .claim("id", user.getId())
                .claim("uid", user.getUid())
                .claim("name", user.getName())
                .claim("role", user.getRole().name()) // Enum의 문자열 값 저장
                .claim("profile",user.getProfileImageUrl())
                .claim("position",user.getPosition())
                .claim("company", user.getCompany().getId())
                .claim("companyName", user.getCompany().getName())
                .claim("companyRate",user.getCompany().getRate())
                .claim("department", user.getDepartment().getId())
                .claim("annualLeaveTotal", user.getAnnualLeaveTotal())
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰으로부터 클레임 추출
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.", e);
        } catch (SignatureException e) {
            throw new IllegalArgumentException("서명이 유효하지 않습니다.", e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다.", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("토큰 검증에 실패했습니다.", e);
        }
    }

    // Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);

        // User 객체 생성
        User user = User.builder()
                .uid(username)
                .role(Role.valueOf(role))
                .build();

        // 권한 생성
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return new UsernamePasswordAuthenticationToken(user, token, authorities);
    }

    // 이메일 인증 토큰 생성
    public String createEmailToken(String email, int days) {
        Date issuedDate = new Date();
        Date expireDate = new Date(issuedDate.getTime() + Duration.ofDays(days).toMillis());

        return Jwts.builder()
                .setIssuer(issuer)
                .setIssuedAt(issuedDate)
                .setExpiration(expireDate)
                .claim("email", email)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 검증 및 이메일 추출
    public String validateAndExtractEmail(String token) {
        Claims claims = getClaims(token); // 클레임 재사용
        return claims.get("email", String.class);
    }

    // 토큰 검증
    public void validateToken(String token) {
        getClaims(token); // 검증은 getClaims 내부에서 처리
    }
}
