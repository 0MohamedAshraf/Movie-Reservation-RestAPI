package com.example.moviereservation.security;

import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.UserRole;
import com.example.moviereservation.support.TestData;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService")
class JwtServiceTest {

    private static final String SECRET = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
    private static final String OTHER_SECRET = "YWJjZGVmMDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODk=";

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "SECRET", SECRET);
        user = TestData.user(1, "jane@example.com", UserRole.CUSTOMER);
    }

    private SecretKey key(String base64Secret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    @Nested
    @DisplayName("[AS-IS] current behavior")
    class AsIs {

        @Test
        @DisplayName("generateToken stores the user id as subject and the role as claim")
        void generateToken() {
            String token = jwtService.generateToken(new HashMap<>(), user);

            assertThat(token).isNotBlank();
            assertThat(jwtService.extractUserId(token)).isEqualTo(1);
            assertThat(jwtService.extractUserRole(token)).isEqualTo(UserRole.CUSTOMER);
        }

        @Test
        @DisplayName("isTokenValid accepts a token generated for the same user")
        void tokenValidForSameUser() {
            String token = jwtService.generateToken(new HashMap<>(), user);

            assertThat(jwtService.isTokenValid(token, user)).isTrue();
        }

        @Test
        @DisplayName("isTokenValid rejects a token generated for another user")
        void tokenInvalidForOtherUser() {
            String token = jwtService.generateToken(new HashMap<>(), user);
            User other = TestData.user(2, "other@example.com");

            assertThat(jwtService.isTokenValid(token, other)).isFalse();
        }

        @Test
        @DisplayName("a freshly generated token is not expired")
        void freshTokenNotExpired() {
            String token = jwtService.generateToken(new HashMap<>(), user);

            assertThat(jwtService.isTokenExpired(token)).isFalse();
        }

        @Test
        @DisplayName("isTokenExpired throws ExpiredJwtException for an expired token (jjwt validates first)")
        void expiredToken() {
            String token = Jwts.builder()
                    .subject("1")
                    .claim("role", UserRole.CUSTOMER.name())
                    .issuedAt(Date.from(Instant.now().minus(Duration.ofHours(2))))
                    .expiration(Date.from(Instant.now().minus(Duration.ofHours(1))))
                    .signWith(key(SECRET), SignatureAlgorithm.HS256)
                    .compact();

            assertThatThrownBy(() -> jwtService.isTokenExpired(token))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        @DisplayName("rejects a malformed token")
        void malformedToken() {
            assertThatThrownBy(() -> jwtService.extractUserId("not-a-jwt"))
                    .isInstanceOf(JwtException.class);
        }

        @Test
        @DisplayName("rejects a token signed with another key")
        void wrongSignature() {
            String token = Jwts.builder()
                    .subject("1")
                    .claim("role", UserRole.CUSTOMER.name())
                    .issuedAt(new Date())
                    .expiration(Date.from(Instant.now().plus(Duration.ofHours(1))))
                    .signWith(key(OTHER_SECRET), SignatureAlgorithm.HS256)
                    .compact();

            assertThatThrownBy(() -> jwtService.extractUserId(token))
                    .isInstanceOf(JwtException.class);
        }

        @Test
        @DisplayName("generated tokens expire at Long.MAX_VALUE (effectively never)")
        void tokenNeverExpiresAsIs() {
            String token = jwtService.generateToken(new HashMap<>(), user);

            // A JWT stores dates with second precision, so Long.MAX_VALUE is truncated.
            assertThat(jwtService.extractExpiration(token).getTime())
                    .isGreaterThan(System.currentTimeMillis() + Duration.ofDays(365L * 100).toMillis());
        }
    }

    @Nested
    @DisplayName("[EXPECTED] correct behavior")
    @Tag("expected")
    class Expected {

        @Test
        @DisplayName("generated tokens should expire after a finite amount of time")
        void tokenShouldHaveFiniteExpiry() {
            String token = jwtService.generateToken(new HashMap<>(), user);

            assertThat(jwtService.extractExpiration(token).getTime())
                    .isLessThan(System.currentTimeMillis() + Duration.ofDays(1).toMillis());
        }
    }
}
