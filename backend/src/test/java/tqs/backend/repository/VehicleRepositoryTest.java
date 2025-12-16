package tqs.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;

@DataJpaTest
@ActiveProfiles("test")
class VehicleRepositoryTest extends tqs.backend.AbstractPostgresTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Vehicle vehicle;

    @BeforeEach
    void setup() {
        bookingRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        User owner = User.builder()
                .email("owner@test.com")
                .fullName("Owner")
                .passwordHash("hash")
                .role(UserRole.OWNER)
            .status("ACTIVE")
            .createdAt(java.time.OffsetDateTime.now())
                .build();
        owner = userRepository.save(owner);

        vehicle = Vehicle.builder()
                .owner(owner)
            .title("Tesla Model 3")
            .brand("Tesla")
                .model("Model 3")
                .city("Porto")
                .year(2022)
                .fuelType("electric")
                .type("sedan")
                .doors(4)
                .exactLocation("Porto center")
                .description("Nice electric car")
                .seats(5)
                .transmission("automatic")
                .licensePlate("AA-00-AA")
                .mileage(15000)
                .pricePerDay(BigDecimal.valueOf(45.0))
            .status("VISIBLE")
                .build();

        vehicle = vehicleRepository.save(vehicle);
    }

    @Test
    void whenFindByCity_thenReturnVehicles() {
        List<Vehicle> result = vehicleRepository.findByCityContainingIgnoreCase("porto");
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getBrand()).isEqualTo("Tesla");
    }

    @Test
    void whenVehicleIsBooked_thenItShouldNotBeAvailableForThatPeriod() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(2);

        OffsetDateTime startDT = start.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endDT = end.atStartOfDay().atOffset(ZoneOffset.UTC);

        Booking booking = new Booking(
            null,
            vehicle,
            vehicle.getOwner(),
            startDT,
            endDT,
            "CONFIRMED",
            BigDecimal.valueOf(90.0),
            "EUR",
            OffsetDateTime.now()
        );
        bookingRepository.save(booking);

        List<Vehicle> available = vehicleRepository.findAvailableVehicles(vehicle.getCity(), start, end);

        assertThat(available).doesNotContain(vehicle);
    }
}
