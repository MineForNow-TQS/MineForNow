package tqs.backend.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Objects;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

import java.util.Collections;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

@DisplayName("JwtUtils Unit Tests")
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private String jwtSecret = "bWluZWZvcm5vd1NlY3JldEtleUZvckp3dFNpZ25pbmdNdXN0QmVMb25nRW5vdWdo"; // Match app.props
    private int jwtExpirationMs = 86400000;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(Objects.requireNonNull(jwtUtils), "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(Objects.requireNonNull(jwtUtils), "jwtExpirationMs", jwtExpirationMs);
    }

    @Test
    @Requirement("SCRUM-32")
    @DisplayName("Generate JWT Token - Should return non-null token string")
    void givenAuthentication_whenGenerateJwtToken_thenReturnsToken() {
        UserDetails userDetails = new User("testuser@test.com", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);

        String token = jwtUtils.generateJwtToken(auth);

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @Requirement("SCRUM-32")
    @DisplayName("Get Username from Token - Should return correct username")
    void givenToken_whenGetUserNameFromJwtToken_thenReturnsUsername() {
        UserDetails userDetails = new User("testuser@test.com", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
        String token = jwtUtils.generateJwtToken(auth);

        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertThat(username).isEqualTo("testuser@test.com");
    }

    @Test
    @Requirement("SCRUM-32")
    @DisplayName("Validate JWT Token - Should return true for valid token")
    void givenValidToken_whenValidateJwtToken_thenReturnsTrue() {
        UserDetails userDetails = new User("testuser@test.com", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
        String token = jwtUtils.generateJwtToken(auth);

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @Requirement("SCRUM-32")
    @DisplayName("Validate JWT Token - Should return false for invalid token")
    void givenInvalidToken_whenValidateJwtToken_thenReturnsFalse() {
        boolean isValid = jwtUtils.validateJwtToken("invalid.token.here");

        assertThat(isValid).isFalse();
    }

    @Test
    @Requirement("SCRUM-32")
    @DisplayName("Validate JWT Token - Should return false for expired token")
    void givenExpiredToken_whenValidateJwtToken_thenReturnsFalse() {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @Requirement("SCRUM-32")
    @DisplayName("Validate JWT Token - Should return false for unsupported token")
    void givenUnsupportedToken_whenValidateJwtToken_thenReturnsFalse() {
        // Create an unsigned token (which should be unsupported when a signed one is
        // expected)
        String token = Jwts.builder()
                .setSubject("testuser")
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @Requirement("SCRUM-32")
    @DisplayName("Validate JWT Token - Should return false for empty token")
    void givenEmptyToken_whenValidateJwtToken_thenReturnsFalse() {
        boolean isValid = jwtUtils.validateJwtToken("");

        assertThat(isValid).isFalse();
    }
}
