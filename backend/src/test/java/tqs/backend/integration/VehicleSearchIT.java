package tqs.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VehicleSearchIT extends tqs.backend.testsupport.AbstractPostgresTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User renter;

    private Vehicle lisboaAvailable;
    private Vehicle lisboaBooked;

    @BeforeEach
    void setup() {
        bookingRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(User.builder()
                .email("owner@test.com")
                .passwordHash("dummy-hash") 
                .fullName("Owner User")
                .role(UserRole.OWNER)
                .build());

        renter = userRepository.save(User.builder()
                .email("renter@test.com")
                .passwordHash("dummy-hash")
                .fullName("Renter User")
                .role(UserRole.RENTER)
                .build());

        lisboaAvailable = vehicleRepository.save(Vehicle.builder()
                .owner(owner)
                .brand("Tesla")
                .model("Model 3")
                .title("Tesla Model 3 - Lisboa")
                .year(2022)
                .licensePlate("AA-00-AA")
                .mileage(10000)
                .fuelType("ELECTRIC")
                .transmission("AUTO")
                .city("Lisboa")
                .exactLocation("Rua A, Lisboa")
                .pricePerDay(BigDecimal.valueOf(80))
                .imageUrl("http://example.com/img1.jpg")
                .description("Available vehicle")
                .status("AVAILABLE")
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        lisboaBooked = vehicleRepository.save(Vehicle.builder()
                .owner(owner)
                .brand("Renault")
                .model("Clio")
                .title("Renault Clio - Lisboa")
                .year(2020)
                .licensePlate("BB-11-BB")
                .mileage(40000)
                .fuelType("GASOLINE")
                .transmission("MANUAL")
                .city("Lisboa")
                .exactLocation("Rua B, Lisboa")
                .pricePerDay(BigDecimal.valueOf(30))
                .imageUrl("http://example.com/img2.jpg")
                .description("Booked vehicle")
                .status("AVAILABLE")
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        LocalDate pickup = LocalDate.of(2025, 12, 10);
        LocalDate dropoff = LocalDate.of(2025, 12, 12);

        bookingRepository.save(Booking.builder()
                .vehicle(lisboaBooked)
                .renter(renter)
                .startDateTime(pickup.atStartOfDay().atOffset(ZoneOffset.UTC))
                .endDateTime(dropoff.atStartOfDay().atOffset(ZoneOffset.UTC))
                .status("CONFIRMED")
                .totalPrice(BigDecimal.valueOf(60))
                .currency("EUR")
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());
    }

    @Test
    void whenSearchVehiclesInCityAndDates_thenOnlyAvailableReturned() {
        String city = "Lisboa";
        LocalDate pickup = LocalDate.of(2025, 12, 10);
        LocalDate dropoff = LocalDate.of(2025, 12, 12);

        String url = String.format("/api/vehicles/search?city=%s&pickup=%s&dropoff=%s",
                city, pickup, dropoff);

        ResponseEntity<VehicleDetailDTO[]> response =
                restTemplate.getForEntity(url, VehicleDetailDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        VehicleDetailDTO[] body = response.getBody();
        assertThat(body).isNotNull();

        List<VehicleDetailDTO> results = Arrays.asList(body);

        assertThat(results)
                .anySatisfy(v -> assertThat(v.getId()).isEqualTo(lisboaAvailable.getId()));

        assertThat(results)
                .noneSatisfy(v -> assertThat(v.getId()).isEqualTo(lisboaBooked.getId()));
    }

    @Test
    void whenSearchWithCityNoMatches_thenEmptyList() {
        String url = "/api/vehicles/search?city=Porto&pickup=2025-12-10&dropoff=2025-12-12";
        ResponseEntity<VehicleDetailDTO[]> response =
                restTemplate.getForEntity(url, VehicleDetailDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }
}
