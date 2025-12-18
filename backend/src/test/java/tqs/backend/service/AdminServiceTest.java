package tqs.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.backend.dto.AdminStatsDTO;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void whenGetDashboardStats_thenReturnCorrectCounts() {
        // Arrange
        when(userRepository.count()).thenReturn(10L);
        when(vehicleRepository.count()).thenReturn(5L);
        when(bookingRepository.count()).thenReturn(20L);
        when(bookingRepository.sumTotalPrice()).thenReturn(1500.0);

        // Act
        AdminStatsDTO stats = adminService.getDashboardStats();

        // Assert
        assertThat(stats.getTotalUsers()).isEqualTo(10L);
        assertThat(stats.getTotalCars()).isEqualTo(5L);
        assertThat(stats.getTotalBookings()).isEqualTo(20L);
        assertThat(stats.getTotalRevenue()).isEqualTo(1500.0);
    }

    @Test
    void whenGetDashboardStats_andRevenueIsNull_thenReturnZeroRevenue() {
        // Arrange
        when(userRepository.count()).thenReturn(10L);
        when(vehicleRepository.count()).thenReturn(5L);
        when(bookingRepository.count()).thenReturn(20L);
        when(bookingRepository.sumTotalPrice()).thenReturn(null);

        // Act
        AdminStatsDTO stats = adminService.getDashboardStats();

        // Assert
        assertThat(stats.getTotalUsers()).isEqualTo(10L);
        assertThat(stats.getTotalCars()).isEqualTo(5L);
        assertThat(stats.getTotalBookings()).isEqualTo(20L);
        assertThat(stats.getTotalRevenue()).isEqualTo(0.0);
    }
}
