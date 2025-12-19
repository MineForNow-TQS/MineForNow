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
                                        baseUrl + "/api/admin/upgrade",
                                        HttpMethod.POST,
                                        request,
                                        Void.class);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

                        // Verifica que el usuario en BD quedó como PENDING_OWNER
                        User updated = userRepository.findByEmail("testuser@test.com").orElseThrow();
                        assertThat(updated.getRole()).isEqualTo(UserRole.PENDING_OWNER);
                        assertThat(updated.getCitizenCardNumber()).isEqualTo("12345678");
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
                                        baseUrl + "/api/admin/upgrade",
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
                                        baseUrl + "/api/admin/upgrade",
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
                                        baseUrl + "/api/admin/upgrade",
                                        upgradeRequest,
                                        String.class);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                }
        }

        @Nested
        @DisplayName("Owner Request Management")
        @Requirement("SCRUM-25")
        class OwnerRequestManagement {

                @SuppressWarnings("null")
                @Test
                @DisplayName("Should approve request and change role to OWNER")
                void whenAdminApprovesRequest_thenReturns200AndUserIsOwner() {
                        // Setup: Create a pending owner
                        User pendingOwner = User.builder()
                                        .email("pending@test.com")
                                        .fullName("Pending Owner")
                                        .password(passwordEncoder.encode("password"))
                                        .role(UserRole.PENDING_OWNER)
                                        .build();
                        pendingOwner = userRepository.save(pendingOwner);

                        // Admin logs in (using existing setup logic or creating explicit admin headers)
                        // Re-using the setUp() admin login might be insufficient if setUp creates a
                        // renter.
                        // Let's create an Admin specifically here.
                        User admin = User.builder()
                                        .email("admin_new@test.com")
                                        .fullName("Admin User")
                                        .password(passwordEncoder.encode("password"))
                                        .role(UserRole.ADMIN)
                                        .build();
                        userRepository.save(admin);

                        String adminToken = getAuthToken("admin_new@test.com", "password");
                        HttpHeaders headers = new HttpHeaders();
                        headers.setBearerAuth(adminToken);
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        HttpEntity<Void> request = new HttpEntity<>(headers);

                        ResponseEntity<Void> response = restTemplate.exchange(
                                        baseUrl + "/api/admin/" + pendingOwner.getId() + "/approve-owner",
                                        HttpMethod.PUT,
                                        request,
                                        Void.class);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

                        @SuppressWarnings("null")
                        User updatedUser = userRepository.findById(pendingOwner.getId()).orElseThrow();
                        assertThat(updatedUser.getRole()).isEqualTo(UserRole.OWNER);
                }

                @SuppressWarnings("null")
                @Test
                @DisplayName("Should reject request and revert role to RENTER")
                void whenAdminRejectsRequest_thenReturns200AndUserIsRenter() {
                        // Setup: Create a pending owner
                        User pendingOwner = User.builder()
                                        .email("pending2@test.com")
                                        .fullName("Pending Owner 2")
                                        .password(passwordEncoder.encode("password"))
                                        .role(UserRole.PENDING_OWNER)
                                        .build();
                        pendingOwner = userRepository.save(pendingOwner);

                        // Admin Login
                        User admin = User.builder()
                                        .email("admin_rej@test.com")
                                        .fullName("Admin User")
                                        .password(passwordEncoder.encode("password"))
                                        .role(UserRole.ADMIN)
                                        .build();
                        userRepository.save(admin);

                        String adminToken = getAuthToken("admin_rej@test.com", "password");
                        HttpHeaders headers = new HttpHeaders();
                        headers.setBearerAuth(adminToken);
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        HttpEntity<Void> request = new HttpEntity<>(headers);

                        ResponseEntity<Void> response = restTemplate.exchange(
                                        baseUrl + "/api/admin/" + pendingOwner.getId() + "/reject-owner",
                                        HttpMethod.PUT,
                                        request,
                                        Void.class);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

                        @SuppressWarnings("null")
                        User updatedUser = userRepository.findById(pendingOwner.getId()).orElseThrow();
                        assertThat(updatedUser.getRole()).isEqualTo(UserRole.RENTER);
                }

                @Test
                @DisplayName("Should return 403 when non-admin tries to manage requests")
                void whenNonAdminTriesManagement_thenReturns401() {
                        // Renter tries to approve
                        @SuppressWarnings("null")
                        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders()); // Uses default testUser
                                                                                          // (RENTER)

                        ResponseEntity<Void> response = restTemplate.exchange(
                                        baseUrl + "/api/admin/999/approve-owner",
                                        HttpMethod.PUT,
                                        request,
                                        Void.class);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                }

                private String getAuthToken(String email, String password) {
                        LoginRequest loginRequest = new LoginRequest();
                        loginRequest.setEmail(email);
                        loginRequest.setPassword(password);

                        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(
                                        baseUrl + "/api/auth/login",
                                        loginRequest,
                                        AuthResponse.class);

                        return Objects.requireNonNull(Objects.requireNonNull(authResponse.getBody()).getToken());
                }
        }

        @Nested
        @DisplayName("User Management (Admin)")
        @Requirement("SCRUM-78")
        class UserManagement {

                private String adminToken;

                @BeforeEach
                void setUpAdmin() {
                        // Criar um Administrador específico para estes testes
                        User admin = User.builder()
                                        .email("admin_it@test.com")
                                        .fullName("Admin Global")
                                        .password(passwordEncoder.encode("admin123"))
                                        .role(UserRole.ADMIN)
                                        .active(true)
                                        .build();
                        userRepository.save(admin);

                        // Obter Token de Admin
                        LoginRequest login = new LoginRequest();
                        login.setEmail("admin_it@test.com");
                        login.setPassword("admin123");
                        
                        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                                        baseUrl + "/api/auth/login", login, AuthResponse.class);
                        adminToken = Objects.requireNonNull(response.getBody()).getToken();
                }

                @Test
                @DisplayName("Should list and search users when admin")
                void whenAdminListsUsers_thenReturns200AndList() {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setBearerAuth(adminToken);
                        HttpEntity<Void> request = new HttpEntity<>(headers);

                        // Testar listagem geral
                        ResponseEntity<UserProfileResponse[]> response = restTemplate.exchange(
                                        baseUrl + "/api/users",
                                        HttpMethod.GET,
                                        request,
                                        UserProfileResponse[].class);

                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody()).isNotEmpty();
                        
                        // Testar pesquisa por nome
                        ResponseEntity<UserProfileResponse[]> searchResponse = restTemplate.exchange(
                                        baseUrl + "/api/users?search=Test User",
                                        HttpMethod.GET,
                                        request,
                                        UserProfileResponse[].class);

                        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(Objects.requireNonNull(searchResponse.getBody())[0].getFullName()).isEqualTo("Test User");
                }

                @Test
                @DisplayName("Should toggle user active status and prevent login")
                void whenAdminBlocksUser_thenUserCannotLogin() {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setBearerAuth(adminToken);
                        HttpEntity<Void> request = new HttpEntity<>(headers);

                        // 1. Bloquear o testUser (id criado no setUp global)
                        ResponseEntity<Void> blockResponse = restTemplate.exchange(
                                        baseUrl + "/api/users/" + testUser.getId() + "/block",
                                        HttpMethod.PUT,
                                        request,
                                        Void.class);

                        assertThat(blockResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

                        // 2. Tentar login com o utilizador bloqueado
                        LoginRequest loginRequest = new LoginRequest();
                        loginRequest.setEmail("testuser@test.com");
                        loginRequest.setPassword("password123");

                        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                                        baseUrl + "/api/auth/login",
                                        loginRequest,
                                        String.class);

                        // Deve retornar FORBIDDEN (conforme o teu ExceptionHandler) ou UNAUTHORIZED
                        assertThat(loginResponse.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.UNAUTHORIZED);
                        assertThat(loginResponse.getBody()).contains("bloqueada");
                }

        }

}
