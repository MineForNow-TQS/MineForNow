package tqs.backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.UserRole; // Importação correta
import tqs.backend.model.Vehicle;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VehicleRepositoryFiltersTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;

    @BeforeEach
    void setup() {
        owner = User.builder()
                .email("owner@test.com")
                .password("pw")
                .role(UserRole.OWNER)
                .fullName("Owner Test")
                .build();
        
        owner = userRepository.save(owner);
    }

    @Test
    void searchAvailableWithFilters_filtersByCityPriceCategoryFuel_andExcludesBookedInRange() {
        // Given
        Vehicle v1 = mkVehicle("Aveiro", 50.0, "SUV", "Diesel", "AA-00-AA");   
        Vehicle v2 = mkVehicle("Aveiro", 80.0, "SUV", "Diesel", "BB-00-BB");   
        Vehicle v3 = mkVehicle("Aveiro", 50.0, "Citadino", "Diesel", "CC-00-CC"); 
        Vehicle v4 = mkVehicle("Porto", 50.0, "SUV", "Diesel", "DD-00-DD");    
        Vehicle v5 = mkVehicle("Aveiro", 50.0, "SUV", "Gasolina", "EE-00-EE"); 
        vehicleRepository.saveAll(List.of(v1, v2, v3, v4, v5));

        LocalDate start = LocalDate.of(2025, 12, 20);
        LocalDate end = LocalDate.of(2025, 12, 22);

        Booking b = new Booking();
        b.setVehicle(v2);
        b.setPickupDate(LocalDate.of(2025, 12, 21));
        b.setReturnDate(LocalDate.of(2025, 12, 23));
        bookingRepository.save(b);

        // When
        var res = vehicleRepository.searchAvailableWithFilters(
                "Aveiro",
                start,
                end,
                40.0,
                60.0,
                List.of("SUV"),
                List.of("Diesel")
        );

        // Then
        assertThat(res).extracting(Vehicle::getLicensePlate)
                .containsExactly("AA-00-AA");
    }

    private Vehicle mkVehicle(String city, double price, String type, String fuel, String plate) {
        Vehicle v = new Vehicle();
        v.setOwner(owner);
        v.setCity(city);
        v.setPricePerDay(price);
        v.setType(type);
        v.setFuelType(fuel);
        v.setBrand("Brand");
        v.setModel("Model");
        v.setLicensePlate(plate);
        return v;
    }
}