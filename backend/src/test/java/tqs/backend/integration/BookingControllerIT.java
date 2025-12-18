package tqs.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import tqs.backend.dto.AuthResponse;
import tqs.backend.dto.BookingDTO;
import tqs.backend.dto.BookingRequestDTO;
import tqs.backend.dto.LoginRequest;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Booking Integration Tests")
class BookingControllerIT {

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
        private Long testVehicleId;
        private Long testUserId;

        @BeforeEach
        void setUp() {
                baseUrl = "http://localhost:" + port + "/api/bookings";

                // Clean up DB strictly in order to avoid FK violations
                bookingRepository.deleteAll();
                vehicleRepository.deleteAll();
                userRepository.deleteAll();

                // Create test user (renter)
                User renter = User.builder()
                                .email("renter@test.com")
                                .fullName("Test Renter")
                                .passwordHash("password123")
                                .role(UserRole.RENTER)
                                .build();
                renter = userRepository.save(Objects.requireNonNull(renter));
                testUserId = renter.getId();

                // Create test vehicle owner
                User owner = User.builder()
                                .email("owner@test.com")
                                .fullName("Test Owner")
                                .passwordHash("password123")
                                .role(UserRole.OWNER)
                                .build();
                owner = userRepository.save(Objects.requireNonNull(owner));

                // Create test vehicle
                Vehicle vehicle = new Vehicle();
                vehicle.setOwner(owner);
                vehicle.setBrand("Test");
                vehicle.setModel("Car");
                vehicle.setYear(2020);
                vehicle.setLicensePlate("TEST-123");
                vehicle.setPricePerDay(BigDecimal.valueOf(100.0));
                vehicle.setCity("Test Location");
                vehicle = vehicleRepository.save(vehicle);
                testVehicleId = vehicle.getId();

                // Login to get auth token
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail("renter@test.com");
                loginRequest.setPassword("password123");

                ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(
                                "http://localhost:" + port + "/api/auth/login",
                                loginRequest,
                                AuthResponse.class);

                authToken = Objects.requireNonNull(authResponse.getBody()).getToken();
        }

        @Test
        @Requirement("SCRUM-15")
        @DisplayName("POST /bookings - Success with valid data")
        void whenCreateBookingWithValidData_thenReturns201() {
                BookingRequestDTO request = new BookingRequestDTO(
                                testVehicleId,
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(5),
                                testUserId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + authToken);
                HttpEntity<BookingRequestDTO> entity = new HttpEntity<>(request, headers);

                ResponseEntity<BookingDTO> response = restTemplate.exchange(
                                baseUrl,
                                HttpMethod.POST,
                                entity,
                                BookingDTO.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                BookingDTO body = response.getBody();
                assertThat(body).isNotNull();
                assertThat(body.getId()).isNotNull();
                assertThat(body.getVehicleId()).isEqualTo(testVehicleId);
                assertThat(body.getRenterId()).isEqualTo(testUserId);
                assertThat(body.getStatus()).isEqualTo("WAITING_PAYMENT");
        }

        @Test
        @Requirement("SCRUM-15")
        @DisplayName("POST /bookings - Failure without authentication")
        void whenCreateBookingWithoutAuth_thenReturns401() {
                BookingRequestDTO request = new BookingRequestDTO(
                                testVehicleId,
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(5),
                                testUserId);

                ResponseEntity<String> response = restTemplate.postForEntity(
                                baseUrl,
                                request,
                                String.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @Requirement("SCRUM-15")
        @DisplayName("POST /bookings - Failure with invalid dates (end before start)")
        void whenCreateBookingWithInvalidDates_thenReturns400() {
                BookingRequestDTO request = new BookingRequestDTO(
                                testVehicleId,
                                LocalDate.now().plusDays(5),
                                LocalDate.now().plusDays(1),
                                testUserId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + authToken);
                HttpEntity<BookingRequestDTO> entity = new HttpEntity<>(request, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                                baseUrl,
                                HttpMethod.POST,
                                entity,
                                String.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @Requirement("SCRUM-15")
        @DisplayName("POST /bookings - Failure with overlapping dates")
        void whenCreateBookingWithOverlappingDates_thenReturns409() {
                // Create first booking
                BookingRequestDTO firstBooking = new BookingRequestDTO(
                                testVehicleId,
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(5),
                                testUserId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + authToken);
                HttpEntity<BookingRequestDTO> entity = new HttpEntity<>(firstBooking, headers);

                restTemplate.exchange(baseUrl, HttpMethod.POST, entity, BookingDTO.class);

                // Try to create overlapping booking
                BookingRequestDTO overlappingBooking = new BookingRequestDTO(
                                testVehicleId,
                                LocalDate.now().plusDays(3),
                                LocalDate.now().plusDays(7),
                                testUserId);

                HttpEntity<BookingRequestDTO> overlappingEntity = new HttpEntity<>(overlappingBooking, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                                baseUrl,
                                HttpMethod.POST,
                                overlappingEntity,
                                String.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @Requirement("SCRUM-15")
        @DisplayName("POST /bookings - Failure with null vehicle ID")
        void whenCreateBookingWithNullVehicleId_thenReturns400() {
                BookingRequestDTO request = new BookingRequestDTO(
                                null,
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(5),
                                testUserId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + authToken);
                HttpEntity<BookingRequestDTO> entity = new HttpEntity<>(request, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                                baseUrl,
                                HttpMethod.POST,
                                entity,
                                String.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @Requirement("SCRUM-15")
        @DisplayName("POST /bookings - Failure with null dates")
        void whenCreateBookingWithNullDates_thenReturns400() {
                BookingRequestDTO request = new BookingRequestDTO(
                                testVehicleId,
                                null,
                                null,
                                testUserId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + authToken);
                HttpEntity<BookingRequestDTO> entity = new HttpEntity<>(request, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                                baseUrl,
                                HttpMethod.POST,
                                entity,
                                String.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
}
