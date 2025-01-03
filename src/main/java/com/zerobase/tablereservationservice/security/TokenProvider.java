package com.zerobase.tablereservationservice.security;

import com.zerobase.tablereservationservice.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;

    @Value("${spring.jwt.secret}")
    private String secretKey;
    private final MemberService memberService;

    /**
     * 키를 생성하는 메서드
     * @return Base64로 인코딩된 강력한 시크릿 키
     */
    public static String generateStrongSecretKey() {
        return Base64.getEncoder().encodeToString(
                Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded()
        );
    }

    /**
     * 토큰 생성(발급)
     *
     * @param username 사용자 이름
     * @param roles    사용자 역할
     * @return JWT 문자열
     */
    public String generateToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        var now = new Date();
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 토큰에서 사용자 이름 추출
     *
     * @param token JWT 문자열
     * @return 사용자 이름
     */
    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    /**
     * 토큰 유효성 검증
     *
     * @param token JWT 문자열
     * @return 유효한 토큰인지 여부
     */
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            log.warn("Token is empty or null");
            return false;
        }

        try {
            Claims claims = this.parseClaims(token);

            boolean isTokenExpired = claims.getExpiration().before(new Date());
            if (isTokenExpired) {
                log.warn("Token is expired");
                return false;
            }

            log.debug("Token is valid. Expiration time: {}", claims.getExpiration());
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("Token is expired. Exception message: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("An error occurred while validating token: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 토큰의 클레임 파싱
     *
     * @param token JWT 문자열
     * @return 클레임 객체
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * SecretKey 생성
     *
     * @return SecretKey 객체
     */
    private SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(this.secretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    /**
     * 인증 객체 생성
     *
     * @param jwt JWT 문자열
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String jwt) {
        Claims claims = this.parseClaims(jwt);
        List<String> roles = claims.get(KEY_ROLES, List.class);

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails userDetails = memberService.loadUserByUsername(this.getUsername(jwt));

        log.debug("Parsed roles: {}", claims.get("roles"));
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
}
