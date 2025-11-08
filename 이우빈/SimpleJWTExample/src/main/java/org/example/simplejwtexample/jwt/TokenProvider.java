package org.example.simplejwtexample.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class TokenProvider {
    private static final String ROLE_CLAIM = "role";
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private static final String DELIMITER = ",";

    private final SecretKey key;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         @Value("${jwt.access-token-validity-in-milliseconds}") long accessTokenValidity,
                         @Value("${jwt.refresh-token-validity-in-milliseconds}") long refreshTokenValidity) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessTokenValidityTime = accessTokenValidity;
        this.refreshTokenValidityTime = refreshTokenValidity;
    }

    public String createAccessToken(Long userId, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidityTime);

        return Jwts.builder()
                .subject(userId.toString())
                .claim(ROLE_CLAIM, role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenValidityTime);

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaim(token);
        String role = claims.get(ROLE_CLAIM).toString();

        List<GrantedAuthority> authorities =
                Arrays.stream(role.split(DELIMITER))
                        .map(SimpleGrantedAuthority::new)
                        .map(authority -> (GrantedAuthority) authority)
                        .toList();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);

        authentication.setDetails(claims);
        return authentication;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER)) {
            return bearer.substring(BEARER.length());
        }

        return null;
    }

    private Claims parseClaim(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (SecurityException e) {
            throw new RuntimeException("토큰 복호화에 실패했습니다.");
        }
    }

    public Long getUserId(String token) {
        Claims claims = parseClaim(token);
        return Long.parseLong(claims.getSubject());
    }
}
