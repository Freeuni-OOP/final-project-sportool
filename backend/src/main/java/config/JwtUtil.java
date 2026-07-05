package config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public final class JwtUtil {

    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L;
    private static final Key SIGNING_KEY = buildSigningKey();

    private JwtUtil() {}

    private static Key buildSigningKey() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            secret = "sportool-dev-jwt-secret-key-min-32-chars!!";
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(String email, int userId, String role, String fullName) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiryDate = new Date(nowMillis + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .claim("fullName", fullName)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SIGNING_KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static int getExpirationSeconds() {
        return (int) (EXPIRATION_MS / 1000);
    }
}
