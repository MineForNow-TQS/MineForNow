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
import tqs.backend.dto.LoginRequest;
import tqs.backend.dto.PaymentDTO;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.time.LocalDate;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Payment Integration Tests")
class PaymentControllerIT {

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
    private Long testBookingId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/bookings";

        // Clean up DB
        bookingRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        User renter = User.builder()
                .email("renter@test.com")
                .fullName("Test Renter")
                .password(passwordEncoder.encode("password123"))
                .role(UserRole.RENTER)
                .build();
        renter = userRepository.save(Objects.requireNonNull(renter));

        User owner = User.builder()
                .email("owner@test.com")
                .fullName("Test Owner")
                .password(passwordEncoder.encode("password123"))
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
        vehicle.setPricePerDay(50.0);
        vehicle.setCity("Test Location");
        vehicle = vehicleRepository.save(vehicle);

        // Create test booking
        Booking booking = new Booking(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                vehicle,
                renter,
                "WAITING_PAYMENT",
                200.0);
        booking = bookingRepository.save(booking);
        testBookingId = booking.getId();

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
    @Requirement("SCRUM-16")
    @DisplayName("POST /bookings/{id}/confirm-payment - Success with valid payment data")
    void whenConfirmPaymentWithValidData_thenReturns200() {
        PaymentDTO paymentData = new PaymentDTO("1234", "John Doe", "12/25", "123");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        HttpEntity<PaymentDTO> entity = new HttpEntity<>(paymentData, headers);

        ResponseEntity<BookingDTO> response = restTemplate.exchange(
                baseUrl + "/" + testBookingId + "/confirm-payment",
                HttpMethod.POST,
                entity,
                BookingDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        BookingDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    @Requirement("SCRUM-16")
    @DisplayName("POST /bookings/{id}/confirm-payment - Failure without authentication")
    void whenConfirmPaymentWithoutAuth_thenReturns401() {
        PaymentDTO paymentData = new PaymentDTO("1234", "John Doe", "12/25", "123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/" + testBookingId + "/confirm-payment",
                paymentData,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Requirement("SCRUM-16")
    @DisplayName("POST /bookings/{id}/confirm-payment - Failure with non-existent booking")
    void whenConfirmPaymentForNonExistentBooking_thenReturns404() {
        PaymentDTO paymentData = new PaymentDTO("1234", "John Doe", "12/25", "123");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        HttpEntity<PaymentDTO> entity = new HttpEntity<>(paymentData, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/999/confirm-payment",
                HttpMethod.POST,
                entity,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Requirement("SCRUM-16")
    @DisplayName("POST /bookings/{id}/confirm-payment - Failure with already confirmed booking")
    void whenConfirmPaymentForAlreadyConfirmedBooking_thenReturns400() {
        // First confirmation
        PaymentDTO paymentData = new PaymentDTO("1234", "John Doe", "12/25", "123");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        HttpEntity<PaymentDTO> entity = new HttpEntity<>(paymentData, headers);

        restTemplate.exchange(
                baseUrl + "/" + testBookingId + "/confirm-payment",
                HttpMethod.POST,
                entity,
                BookingDTO.class);

        // Try to confirm again
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + testBookingId + "/confirm-payment",
                HttpMethod.POST,
                entity,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("not waiting for payment");
    }
}
