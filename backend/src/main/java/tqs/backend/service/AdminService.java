package tqs.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tqs.backend.dto.AdminStatsDTO;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;

    public AdminStatsDTO getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalCars = vehicleRepository.count();
        long totalBookings = bookingRepository.count();
        Double totalRevenue = bookingRepository.sumTotalPrice();

        if (totalRevenue == null) {
            totalRevenue = 0.0;
        }

        return AdminStatsDTO.builder()
                .totalUsers(totalUsers)
                .totalCars(totalCars)
                .totalBookings(totalBookings)
                .totalRevenue(totalRevenue)
                .build();
    }
}
