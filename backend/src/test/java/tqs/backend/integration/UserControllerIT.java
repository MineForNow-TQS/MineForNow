package tqs.backend.integration;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import tqs.backend.dto.AuthResponse;
import tqs.backend.dto.LoginRequest;
import tqs.backend.dto.UpdateProfileRequest;
import tqs.backend.dto.UpgradeOwnerRequest;
import tqs.backend.dto.UserProfileResponse;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("User Profile Integration Tests")
@Requirement("SCRUM-46")
class UserControllerIT {

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
    private String authToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        // Clean up DB strictly in order to avoid FK violations
        bookingRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = User.builder()
                .email("testuser@test.com")
                .fullName("Test User")
                .password(passwordEncoder.encode("password123"))
                .role(UserRole.RENTER)
                .phone("+351912345678")
                .drivingLicense("AB123456")
                .build();
        testUser = userRepository.save(Objects.requireNonNull(testUser));

        // Login to get JWT token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser@test.com");
        loginRequest.setPassword("password123");

        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(
                baseUrl + "/api/auth/login",
                loginRequest,
                AuthResponse.class);

        authToken = Objects.requireNonNull(Objects.requireNonNull(authResponse.getBody()).getToken());
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(authToken));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Nested
    @DisplayName("GET /api/users/me")
    class GetCurrentUser {

        @Test
        @DisplayName("Should return current user profile with valid token")
        void whenGetMeWithValidToken_thenReturns200AndProfile() {
            HttpEntity<Void> request = new HttpEntity<>(Objects.requireNonNull(createAuthHeaders()));

            ResponseEntity<UserProfileResponse> response = restTemplate.exchange(
                    baseUrl + "/api/users/me",
                    HttpMethod.GET,
                    request,
                    UserProfileResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            UserProfileResponse body = Objects.requireNonNull(response.getBody());
            assertThat(body.getEmail()).isEqualTo("testuser@test.com");
            assertThat(body.getFullName()).isEqualTo("Test User");
            assertThat(body.getPhone()).isEqualTo("+351912345678");
            assertThat(body.getDrivingLicense()).isEqualTo("AB123456");
            assertThat(body.getRole()).isEqualTo(UserRole.RENTER);
        }

        @Test
        @DisplayName("Should return 401 when no token provided")
        void whenGetMeWithoutToken_thenReturns401() {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/api/users/me",
                    String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Should return 401 when invalid token provided")
        void whenGetMeWithInvalidToken_thenReturns401() {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth("invalid-token");
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/api/users/me",
                    HttpMethod.GET,
                    request,
                    String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @Nested
    @DisplayName("PUT /api/users/me")
    class UpdateCurrentUser {

        @Test
        @DisplayName("Should update phone and driving license successfully")
        void whenUpdateProfileWithValidData_thenReturns200AndUpdatedProfile() {
            UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                    .phone("+351999888777")
                    .drivingLicense("NEW12345")
                    .build();

            HttpEntity<UpdateProfileRequest> request = new HttpEntity<>(updateRequest, createAuthHeaders());

            ResponseEntity<UserProfileResponse> response = restTemplate.exchange(
                    baseUrl + "/api/users/me",
                    HttpMethod.PUT,
                    request,
                    UserProfileResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            UserProfileResponse body = Objects.requireNonNull(response.getBody());
            assertThat(body.getPhone()).isEqualTo("+351999888777");
            assertThat(body.getDrivingLicense()).isEqualTo("NEW12345");
            // Other fields should remain unchanged
            assertThat(body.getEmail()).isEqualTo("testuser@test.com");
            assertThat(body.getFullName()).isEqualTo("Test User");
        }

        @Test
        @DisplayName("Should update only phone when only phone provided")
        void whenUpdateOnlyPhone_thenOnlyPhoneIsUpdated() {
            UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                    .phone("+351111222333")
                    .build();

            HttpEntity<UpdateProfileRequest> request = new HttpEntity<>(updateRequest, createAuthHeaders());

            ResponseEntity<UserProfileResponse> response = restTemplate.exchange(
                    baseUrl + "/api/users/me",
                    HttpMethod.PUT,
                    request,
                    UserProfileResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            UserProfileResponse body = Objects.requireNonNull(response.getBody());
            assertThat(body.getPhone()).isEqualTo("+351111222333");
            assertThat(body.getDrivingLicense()).isEqualTo("AB123456"); // unchanged
        }

        @Test
        @DisplayName("Should return 401 when no token provided for update")
        void whenUpdateWithoutToken_thenReturns401() {
            UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                    .phone("+351999888777")
                    .build();

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/api/users/me",
                    HttpMethod.PUT,
                    new HttpEntity<>(Objects.requireNonNull(updateRequest)),
                    String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Should return 401 when invalid token provided for update")
        void whenUpdateWithInvalidToken_thenReturns401() {
            UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                    .phone("+351999888777")
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth("invalid-token");
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            HttpEntity<UpdateProfileRequest> request = new HttpEntity<>(updateRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/api/users/me",
                    HttpMethod.PUT,
                    request,
                    String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

        @Nested
        @DisplayName("POST /api/users/upgrade")
        class RequestOwnerUpgrade {

        @Test
        @DisplayName("Should submit upgrade request successfully")
        void whenValidUpgradeRequest_thenReturns200() {
            UpgradeOwnerRequest upgradeRequest = new UpgradeOwnerRequest();
            upgradeRequest.setPhone("+351912345678");
            upgradeRequest.setCitizenCardNumber("12345678");
            upgradeRequest.setDrivingLicense("AB123456");
            upgradeRequest.setMotivation("Quero ser proprietário");

            HttpEntity<UpgradeOwnerRequest> request = new HttpEntity<>(upgradeRequest, createAuthHeaders());

            ResponseEntity<Void> response = restTemplate.exchange(
                    baseUrl + "/api/users/upgrade",
                    HttpMethod.POST,
                    request,
                    Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            // Verifica que el usuario en BD quedó como PENDING_OWNER
            User updated = userRepository.findByEmail("testuser@test.com").orElseThrow();
            assertThat(updated.getRole()).isEqualTo(UserRole.PENDING_OWNER);
            assertThat(updated.getCitizenCardNumber()).isEqualTo("12345678");
            assertThat(updated.getMotivation()).isEqualTo("Quero ser proprietário");
        }

        @Test
        @DisplayName("Should return 400 when driving license invalid")
        void whenInvalidDrivingLicense_thenReturns400() {
            UpgradeOwnerRequest upgradeRequest = new UpgradeOwnerRequest();
            upgradeRequest.setPhone("+351912345678");
            upgradeRequest.setCitizenCardNumber("12345678");
            upgradeRequest.setDrivingLicense("123456"); // inválido
            upgradeRequest.setMotivation("Motivação");

            HttpEntity<UpgradeOwnerRequest> request = new HttpEntity<>(upgradeRequest, createAuthHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/api/users/upgrade",
                    HttpMethod.POST,
                    request,
                    String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).contains("Formato inválido da carta de condução");
        }

        @Test
        @DisplayName("Should return 409 when user already owner")
        void whenAlreadyOwner_thenReturns409() {
            // Cambia el rol del usuario a OWNER antes de llamar
            testUser.setRole(UserRole.OWNER);
            userRepository.save(testUser);

            UpgradeOwnerRequest upgradeRequest = new UpgradeOwnerRequest();
            upgradeRequest.setPhone("+351912345678");
            upgradeRequest.setCitizenCardNumber("12345678");
            upgradeRequest.setDrivingLicense("AB123456");
            upgradeRequest.setMotivation("Motivação");

            HttpEntity<UpgradeOwnerRequest> request = new HttpEntity<>(upgradeRequest, createAuthHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/api/users/upgrade",
                    HttpMethod.POST,
                    request,
                    String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).contains("Pedido já submetido ou utilizador já é Owner");
        }

        @Test
        @DisplayName("Should return 401 when no token provided")
        void whenUpgradeWithoutToken_thenReturns401() {
            UpgradeOwnerRequest upgradeRequest = new UpgradeOwnerRequest();
            upgradeRequest.setPhone("+351912345678");
            upgradeRequest.setCitizenCardNumber("12345678");
            upgradeRequest.setDrivingLicense("AB123456");
            upgradeRequest.setMotivation("Motivação");

            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl + "/api/users/upgrade",
                    upgradeRequest,
                    String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

}
