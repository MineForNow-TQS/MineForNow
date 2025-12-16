package tqs.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Objects;

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
import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Vehicle Get By Id Integration Tests")
class VehicleGetByIdIT extends tqs.backend.AbstractPostgresTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;
    private Long vehicleId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/vehicles";

        bookingRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        User owner = User.builder()
                .email("owner@test.com")
                .fullName("Vehicle Owner")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(UserRole.OWNER)
                .build();
        owner = userRepository.save(Objects.requireNonNull(owner));

        Vehicle vehicle = Vehicle.builder()
                .owner(owner)
            .title("Tesla Model 3")
            .brand("Tesla")
                .model("Model 3")
                .city("Porto")
                .year(2022)
                .fuelType("Electric")
                .seats(5)
                .transmission("Automatic")
                .licensePlate("AA-00-AA")
                .mileage(15000)
                .pricePerDay(BigDecimal.valueOf(45.0))
            .status("VISIBLE")
                .build();

        vehicleId = vehicleRepository.save(vehicle).getId();
    }

    @Test
    @Requirement("SCRUM-31")
    @DisplayName("GET /vehicles/{id} - returns vehicle details when exists")
    void whenGetVehicleById_thenReturns200AndVehicleDetails() {
        ResponseEntity<VehicleDetailDTO> response =
                restTemplate.getForEntity(baseUrl + "/" + vehicleId, VehicleDetailDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        VehicleDetailDTO dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(vehicleId);
        assertThat(dto.getBrand()).isEqualTo("Tesla");
        assertThat(dto.getModel()).isEqualTo("Model 3");
        assertThat(dto.getCity()).isEqualTo("Porto");
        assertThat(dto.getYear()).isEqualTo(2022);
        assertThat(dto.getPricePerDay().doubleValue()).isEqualTo(45.0);
    }

    @Test
    @Requirement("SCRUM-31")
    @DisplayName("GET /vehicles/{id} - returns 404 when vehicle not found")
    void whenGetVehicleByInvalidId_thenReturns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/999999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
