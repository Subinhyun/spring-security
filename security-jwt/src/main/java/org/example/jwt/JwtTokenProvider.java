package org.example.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.example.repository.RefreshTokenRepository;
import org.example.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private static String key = "c3ByaW5nLWJvb3Qtc2VjdXJpdHktand0LXR1dG9yaWFsLWppd29vbi1zcHJpbmctYm9vdC1zZWN1cml0eS1qd3QtdHV0b3JpYWwK";;
    private long tokenValidTime = 30 * 60 * 1000;
    private long refreshTokenValidTime = 7 * 60 * 60 * 1000;
    private long accessTokenValidTime = 30 * 60 * 1000;

    @Autowired
    private final UserDetailsService userDetailsService;

    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;

    protected void init(@Value("${jwt.secret}") String secretKey) {
        key = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String userPk, long tokenValidTime) {
        Claims claims = Jwts.claims().setSubject(userPk);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public String createAccessToken(String userPk) {
        return createToken(userPk, accessTokenValidTime);
    }

    public String createRefreshToken(String userPk) {
        return createToken(userPk, refreshTokenValidTime);
    }

    public Token getToken(String userPk) {
        return Token.builder()
                .accessToken(createAccessToken(userPk))
                .refreshToken(createRefreshToken(userPk))
                .build();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveAccessToken(HttpServletRequest request) {
        if (request.getHeader("authorization") != null)
            return request.getHeader("authorization");
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        if (request.getHeader("refreshToken") != null)
            return request.getHeader("refreshToken");
        return null;
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean existsRefreshToken(String refreshToken) {
        return refreshTokenRepository.existsByValue(refreshToken);
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("authorization", "bearer "+ accessToken);
    }


}
