package org.example.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        if (accessToken != null) {
            if (jwtTokenProvider.validateToken(accessToken)) {
                this.setAuthentication(accessToken);
            }
            else if (!jwtTokenProvider.validateToken(accessToken) && refreshToken != null) {
                boolean validateRefreshToken = jwtTokenProvider.validateToken(refreshToken);
                boolean isRefreshToken = jwtTokenProvider.existsRefreshToken(refreshToken);
                if (validateRefreshToken && isRefreshToken) {
                    String email = jwtTokenProvider.getUserPk(refreshToken);
                    /// 이메일로 권한정보 받아오기
//                    List<String> roles = jwtTokenProvider.getRoles(email);
                    String newAccessToken = jwtTokenProvider.createAccessToken(email);
                    jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);
                    this.setAuthentication(newAccessToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
    public void setAuthentication(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

