package com.zerobase.tablereservationservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    /**
     * 요청 처리 메서드 
     * JWT 토큰을 추출하고 검증하여, 인증 객체 설정
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = this.resoleveTokenFromRequest(request);
        log.debug("Extracted token: {}", token);

        if (StringUtils.hasText(token)) {
            log.debug("Validating token");

            if (this.tokenProvider.validateToken(token)) {
                log.debug("Token is valid. Getting authentication");
                Authentication auth = tokenProvider.getAuthentication(token);

                if (auth != null) {
                    log.debug("Authentication successful: {}", auth);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    log.warn("Authentication failed. Auth is null.");
                }
            } else {
                log.warn("Invalid token.");
            }
        } else {
            log.warn("No token found in request.");
        }
        filterChain.doFilter(request, response);

    }

    /**
     * 요청에서 JWT 토큰 추출
     * Authorization 헤더에서 "Bearer " 접두사를 제외한 토큰을 반환.
     * @param request 
     * @return 토큰 문자열
     */
    private String resoleveTokenFromRequest(HttpServletRequest request) {

        String token = request.getHeader(TOKEN_HEADER);

        if(!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
