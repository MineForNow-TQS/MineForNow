package tqs.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.time.LocalDate;

@Configuration
// @Profile("dev") // Disabled - create data manually via UI
public class TestDataInitializer {

    @Bean
    CommandLineRunner initTestData(
            UserRepository userRepository,
            VehicleRepository vehicleRepository,
            BookingRepository bookingRepository) {

        return args -> {
            // Check if owner already exists
            if (userRepository.findByEmail("owner@minefornow.com").isPresent()) {
                System.out.println("✅ Test data already initialized");
                return;
            }

            System.out.println("🔧 Initializing test data...");

            // Create owner
            User owner = new User();
            owner.setEmail("owner@minefornow.com");
            owner.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"); // password123
            owner.setFullName("Test Owner");
            owner.setRole(UserRole.OWNER);
            owner = userRepository.save(owner);

            // Create renter
            User renter = new User();
            renter.setEmail("renter@minefornow.com");
            renter.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"); // password123
            renter.setFullName("Test Renter");
            renter.setRole(UserRole.RENTER);
            renter = userRepository.save(renter);

            // Create 3 vehicles
            for (int i = 1; i <= 3; i++) {
                Vehicle vehicle = new Vehicle();
                vehicle.setOwner(owner);
                vehicle.setBrand("Mercedes-Benz");
                vehicle.setModel("AMG GT " + i);
                vehicle.setYear(2021 + i);
                vehicle.setLicensePlate("AA-00-0" + i);
                vehicle.setSeats(2);
                vehicle.setTransmission("Automatic");
                vehicle.setFuelType("Gasoline");
                vehicle.setPricePerDay(850.0);
                vehicleRepository.save(vehicle);
            }

            // Get first vehicle for bookings
            Vehicle vehicle = vehicleRepository.findByOwnerEmail(owner.getEmail()).get(0);

            // Create 3 bookings
            Booking booking1 = new Booking();
            booking1.setPickupDate(LocalDate.of(2025, 12, 22));
            booking1.setReturnDate(LocalDate.of(2025, 12, 25));
            booking1.setStatus("CONFIRMED");
            booking1.setTotalPrice(850.0);
            booking1.setVehicle(vehicle);
            booking1.setRenter(renter);
            bookingRepository.save(booking1);

            Booking booking2 = new Booking();
            booking2.setPickupDate(LocalDate.of(2025, 12, 26));
            booking2.setReturnDate(LocalDate.of(2025, 12, 27));
            booking2.setStatus("CONFIRMED");
            booking2.setTotalPrice(1700.0);
            booking2.setVehicle(vehicle);
            booking2.setRenter(renter);
            bookingRepository.save(booking2);

            Booking booking3 = new Booking();
            booking3.setPickupDate(LocalDate.of(2025, 12, 28));
            booking3.setReturnDate(LocalDate.of(2025, 12, 30));
            booking3.setStatus("WAITING_PAYMENT");
            booking3.setTotalPrice(1100.0);
            booking3.setVehicle(vehicle);
            booking3.setRenter(renter);
            bookingRepository.save(booking3);

            System.out.println("✅ Test data initialized successfully!");
            System.out.println("   - Owner: owner@minefornow.com / password123");
            System.out.println("   - Renter: renter@minefornow.com / password123");
            System.out.println("   - 3 Vehicles");
            System.out.println("   - 3 Bookings (2 CONFIRMED, 1 WAITING_PAYMENT)");
        };
    }
}
