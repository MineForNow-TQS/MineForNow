package tqs.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.backend.dto.DashboardStatsDTO;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private VehicleRepository vehicleRepository;

        @Mock
        private BookingRepository bookingRepository;

        @InjectMocks
        private DashboardService dashboardService;

        private User owner;
        private Vehicle vehicle1;
        private Vehicle vehicle2;
        private Booking confirmedBooking;
        private Booking pendingBooking;
        private Booking cancelledBooking;

        @BeforeEach
        void setUp() {
                // Setup owner
                owner = new User();
                owner.setId(1L);
                owner.setEmail("owner@test.com");

                // Setup vehicles
                vehicle1 = new Vehicle();
                vehicle1.setId(1L);
                vehicle1.setBrand("Toyota");
                vehicle1.setModel("Corolla");
                vehicle1.setYear(2020);
                vehicle1.setOwner(owner);

                vehicle2 = new Vehicle();
                vehicle2.setId(2L);
                vehicle2.setBrand("Honda");
                vehicle2.setModel("Civic");
                vehicle2.setYear(2021);
                vehicle2.setOwner(owner);

                // Setup bookings
                User renter = new User();
                renter.setId(2L);
                renter.setEmail("renter@test.com");

                confirmedBooking = new Booking(
                                LocalDate.of(2025, 1, 1),
                                LocalDate.of(2025, 1, 5),
                                vehicle1,
                                renter,
                                "CONFIRMED",
                                500.0);
                confirmedBooking.setId(1L);

                pendingBooking = new Booking(
                                LocalDate.of(2025, 2, 1),
                                LocalDate.of(2025, 2, 5),
                                vehicle2,
                                renter,
                                "WAITING_PAYMENT",
                                300.0);
                pendingBooking.setId(2L);

                cancelledBooking = new Booking(
                                LocalDate.of(2025, 3, 1),
                                LocalDate.of(2025, 3, 5),
                                vehicle1,
                                renter,
                                "CANCELLED",
                                200.0);
                cancelledBooking.setId(3L);
        }

        @Test
        void getOwnerStats_Success() {
                // Given
                when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
                when(vehicleRepository.findByOwnerEmail("owner@test.com"))
                                .thenReturn(Arrays.asList(vehicle1, vehicle2));
                when(bookingRepository.findAll())
                                .thenReturn(Arrays.asList(confirmedBooking, pendingBooking, cancelledBooking));

                // When
                DashboardStatsDTO stats = dashboardService.getOwnerStats("owner@test.com");

                // Then
                assertThat(stats).isNotNull();
                assertThat(stats.getTotalRevenue()).isEqualTo(500.0); // Only confirmed
                assertThat(stats.getActiveVehicles()).isEqualTo(2);
                assertThat(stats.getPendingBookings()).isEqualTo(1);
                assertThat(stats.getCompletedBookings()).isEqualTo(1);
        }

        @Test
        void getOwnerStats_NoVehicles() {
                // Given
                when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
                when(vehicleRepository.findByOwnerEmail("owner@test.com")).thenReturn(List.of());
                when(bookingRepository.findAll()).thenReturn(List.of());

                // When
                DashboardStatsDTO stats = dashboardService.getOwnerStats("owner@test.com");

                // Then
                assertThat(stats).isNotNull();
                assertThat(stats.getTotalRevenue()).isEqualTo(0.0);
                assertThat(stats.getActiveVehicles()).isEqualTo(0);
                assertThat(stats.getPendingBookings()).isEqualTo(0);
                assertThat(stats.getCompletedBookings()).isEqualTo(0);
        }

        @Test
        void getOwnerStats_MultipleConfirmedBookings() {
                // Given
                Booking confirmedBooking2 = new Booking(
                                LocalDate.of(2025, 4, 1),
                                LocalDate.of(2025, 4, 5),
                                vehicle2,
                                new User(),
                                "CONFIRMED",
                                700.0);

                when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
                when(vehicleRepository.findByOwnerEmail("owner@test.com"))
                                .thenReturn(Arrays.asList(vehicle1, vehicle2));
                when(bookingRepository.findAll())
                                .thenReturn(Arrays.asList(confirmedBooking, confirmedBooking2, pendingBooking));

                // When
                DashboardStatsDTO stats = dashboardService.getOwnerStats("owner@test.com");

                // Then
                assertThat(stats.getTotalRevenue()).isEqualTo(1200.0); // 500 + 700
                assertThat(stats.getCompletedBookings()).isEqualTo(2);
                assertThat(stats.getPendingBookings()).isEqualTo(1);
        }

        @Test
        void getOwnerStats_UserNotFound() {
                // Given
                when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

                // When & Then
                assertThatThrownBy(() -> dashboardService.getOwnerStats("unknown@test.com"))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Owner not found");
        }

        @Test
        void getOwnerStats_OnlyPendingBookings() {
                // Given
                when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
                when(vehicleRepository.findByOwnerEmail("owner@test.com"))
                                .thenReturn(Arrays.asList(vehicle1, vehicle2));
                when(bookingRepository.findAll())
                                .thenReturn(Arrays.asList(pendingBooking));

                // When
                DashboardStatsDTO stats = dashboardService.getOwnerStats("owner@test.com");

                // Then
                assertThat(stats.getTotalRevenue()).isEqualTo(0.0); // No confirmed
                assertThat(stats.getPendingBookings()).isEqualTo(1);
                assertThat(stats.getCompletedBookings()).isEqualTo(0);
        }
}
