package tqs.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tqs.backend.dto.AdminStatsDTO;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;
import tqs.backend.dto.UserProfileResponse;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

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

    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserProfileResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .drivingLicense(user.getDrivingLicense())
                        .citizenCardNumber(user.getCitizenCardNumber())
                        .ownerMotivation(user.getOwnerMotivation())
                        .role(user.getRole())
                        .build())
                .toList();
    }

    @Transactional
    public void approveOwnerRequest(Long userId) {
        @SuppressWarnings("null")
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        if (user.getRole() != UserRole.PENDING_OWNER) {
            throw new IllegalStateException("Utilizador não tem pedido pendente");
        }

        user.setRole(UserRole.OWNER);
        userRepository.save(user);
    }

    @Transactional
    public void rejectOwnerRequest(Long userId) {
        @SuppressWarnings("null")
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        if (user.getRole() != UserRole.PENDING_OWNER) {
            throw new IllegalStateException("Utilizador não tem pedido pendente");
        }

        // Revert to RENTER
        user.setRole(UserRole.RENTER);
        userRepository.save(user);
    }
}
