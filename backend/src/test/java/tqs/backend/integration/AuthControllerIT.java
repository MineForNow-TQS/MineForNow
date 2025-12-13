package tqs.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import tqs.backend.dto.AuthResponse;
import tqs.backend.dto.LoginRequest;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Authentication Integration Tests")
class AuthControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/auth/login";

        // Clean up DB strictly in order to avoid FK violations
        bookingRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        User user = User.builder()
                .email("testrenter@test.com")
                .fullName("Test Renter")
                .password(passwordEncoder.encode("password123"))
                .role(UserRole.RENTER)
                .build();
        userRepository.save(user);
    }

    @Test
    @Requirement("SCRUM-32")
    @DisplayName("POST /login - Success with correct credentials")
    void whenLoginWithValidCredentials_thenReturns200AndToken() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testrenter@test.com");
        loginRequest.setPassword("password123");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                baseUrl,
                loginRequest,
                AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
        assertThat(response.getBody().getType()).isEqualTo("Bearer");
    }

    @Test
    @Requirement("SCRUM-32")
    @DisplayName("POST /login - Failure with incorrect password")
    void whenLoginWithInvalidPassword_thenReturns401() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testrenter@test.com");
        loginRequest.setPassword("wrongpassword");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                baseUrl,
                loginRequest,
                AuthResponse.class);

        // Spring Security typically returns 401 Unauthorized for bad credentials
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Requirement("SCRUM-32")
    @DisplayName("POST /login - Failure with non-existent user")
    void whenLoginWithNonExistentUser_thenReturns401() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("unknown@test.com");
        loginRequest.setPassword("password123");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(baseUrl, loginRequest, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
