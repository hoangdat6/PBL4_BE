package org.pbl4.pbl4_be.security.jwt;
import java.security.Key;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.pbl4.pbl4_be.models.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${pbl4.app.jwtSecret}")
    private String jwtSecret;

    @Value("${pbl4.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${pbl4.app.jwtCookieName}")
    private String jwtCookieName;

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    // Tạo JWT và lưu trữ trong cookie
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromEmail(userPrincipal.getEmail());
        return ResponseCookie.from(jwtCookieName, jwt)
                .sameSite("None")  // Chỉ gửi cookie cho các trang web cùng một nguồn gốc
                .path("/")  // Chỉ gửi cookie cho các đường dẫn `/`
                .maxAge(jwtExpirationMs / 1000)  // Chuyển ms thành giây
                .secure(true)
                .httpOnly(true)
                .build();
    }
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookieName, "")
                .sameSite("None")
                .path("/")
                .maxAge(0)    // Đặt tuổi thọ cookie là 0 để xóa ngay lập tức
                .httpOnly(true)
                .secure(true)
                .build();
    }


    // Tạo token từ email
    public String generateTokenFromEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Lấy email từ token
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Kiểm tra tính hợp lệ của token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
