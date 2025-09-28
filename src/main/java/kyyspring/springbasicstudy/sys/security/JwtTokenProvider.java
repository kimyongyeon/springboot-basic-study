package kyyspring.springbasicstudy.sys.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import kyyspring.springbasicstudy.biz.login.domain.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    // ✅ 환경변수로 관리
    @Value("${jwt.secret:MyVeryLongSecretKeyForJWTHS512Algorithm1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ}")
    private String jwtSecretBase64;

    @Value("${jwt.expiration:86400000}")
    private int jwtExpirationMs;

    private SecretKey secretKey;


    @PostConstruct
    public void initializeSecretKey() {
        // ✅ 복잡한 Base64 처리 없이 안전한 키 자동 생성
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        logger.info("JWT SecretKey auto-generated for HS512 algorithm");
    }

    public String generateToken(String email, UserType userType) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

            return Jwts.builder()
                    .setSubject(email)
                    .claim("userType", userType.name())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(secretKey)
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating JWT token for email: {}", email, e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public String getEmailFromToken(String token) {
        try {
            token = cleanToken(token);
            Claims claims = parseToken(token);
            return claims.getSubject();
        } catch (JwtException e) {
            logger.error("Failed to extract email from token", e);
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage(), e);
        }
    }

    public UserType getUserTypeFromToken(String token) {
        try {
            token = cleanToken(token);
            Claims claims = parseToken(token);
            String userTypeStr = claims.get("userType", String.class);

            if (userTypeStr == null) {
                throw new IllegalArgumentException("UserType claim not found in token");
            }

            return UserType.valueOf(userTypeStr);
        } catch (JwtException e) {
            logger.error("Failed to extract user type from token", e);
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("UserType")) {
                throw e;
            }
            logger.error("Invalid user type in token", e);
            throw new IllegalArgumentException("Invalid user type in token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            token = cleanToken(token);

            // ✅ 기본 형식 검증
            if (!isValidTokenFormat(token)) {
                logger.warn("Invalid token format");
                return false;
            }

            // ✅ JWT 파싱 및 서명 검증
            Claims claims = parseToken(token);

            // ✅ 만료 시간 확인 (Jwts.parser()가 자동으로 하지만 명시적 확인)
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                logger.warn("Token has expired");
                return false;
            }

            return true;
        } catch (JwtException e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during token validation", e);
            return false;
        }
    }

    // ✅ 토큰 만료 시간 확인
    public boolean isTokenExpired(String token) {
        try {
            token = cleanToken(token);
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true; // 파싱 실패 시 만료된 것으로 간주
        }
    }

    // ✅ 토큰 남은 시간 (밀리초)
    public long getTokenRemainingTime(String token) {
        try {
            token = cleanToken(token);
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return Math.max(0, expiration.getTime() - System.currentTimeMillis());
        } catch (JwtException e) {
            return 0;
        }
    }

    // ✅ 공통 토큰 파싱 메서드
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ 토큰 정리 (공백, 개행 제거)
    private String cleanToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return token.trim();
    }

    // ✅ 기본 토큰 형식 검증
    private boolean isValidTokenFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }

        // 각 부분이 Base64URL 형식인지 확인
        for (String part : parts) {
            if (!part.matches("^[A-Za-z0-9_-]*$")) {
                return false;
            }
        }

        return true;
    }
}