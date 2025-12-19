package tqs.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AdminControllerIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private VehicleRepository vehicleRepository;

        @Autowired
        private BookingRepository bookingRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @SuppressWarnings("null")
        @BeforeEach
        void setUp() {
                // Clean DB
                bookingRepository.deleteAll();
                vehicleRepository.deleteAll();
                userRepository.deleteAll();

                // Create Users
                User admin = User.builder()
                                .email("admin@test.com")
                                .fullName("Admin User")
                                .password(passwordEncoder.encode("password"))
                                .role(UserRole.ADMIN)
                                .build();
                userRepository.save(admin);

                User renter = User.builder()
                                .email("renter@test.com")
                                .fullName("Renter User")
                                .password(passwordEncoder.encode("password"))
                                .role(UserRole.RENTER)
                                .build();
                userRepository.save(renter);

                User owner = User.builder()
                                .email("owner@test.com")
                                .fullName("Owner User")
                                .password(passwordEncoder.encode("password"))
                                .role(UserRole.OWNER)
                                .build();
                owner = userRepository.save(owner);

                // Create Vehicle
                Vehicle vehicle = new Vehicle();
                vehicle.setOwner(owner);
                vehicle.setBrand("Test Brand");
                vehicle.setModel("Test Model");
                vehicle.setYear(2022);
                vehicle.setLicensePlate("AA-00-00");
                vehicle.setPricePerDay(100.0);
                vehicle.setCity("Test City");
                vehicle = vehicleRepository.save(vehicle);

                // Create Booking
                Booking booking = new Booking();
                booking.setRenter(renter);
                booking.setVehicle(vehicle);
                booking.setPickupDate(LocalDate.now());
                booking.setReturnDate(LocalDate.now().plusDays(2));
                booking.setTotalPrice(200.0);
                booking.setStatus("CONFIRMED");
                bookingRepository.save(booking);
        }

        @Test
        @WithMockUser(username = "admin@test.com", roles = "ADMIN")
        void whenGetStatsAsAdmin_thenReturnCorrectStats() throws Exception {
                mockMvc.perform(get("/api/admin/stats"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalUsers").value(3)) // admin, renter, owner
                                .andExpect(jsonPath("$.totalCars").value(1))
                                .andExpect(jsonPath("$.totalBookings").value(1))
                                .andExpect(jsonPath("$.totalRevenue").value(200.0));
        }

        @Test
        @WithMockUser(username = "renter@test.com", roles = "USER")
        void whenGetStatsAsUser_thenReturnForbidden() throws Exception {
                mockMvc.perform(get("/api/admin/stats"))
                                .andExpect(status().isForbidden());
        }

        @Test
        void whenGetStatsUnauthenticated_thenReturnUnauthorized() throws Exception {
                mockMvc.perform(get("/api/admin/stats"))
                                .andExpect(status().isUnauthorized());
        }
}
