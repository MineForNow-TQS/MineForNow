package tqs.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import tqs.backend.dto.BookingDTO;
import tqs.backend.dto.DashboardStatsDTO;
import tqs.backend.model.Booking;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

@Service
public class DashboardService {

        private final UserRepository userRepository;

        private final VehicleRepository vehicleRepository;

        private final BookingRepository bookingRepository;

        public DashboardService(UserRepository userRepository, VehicleRepository vehicleRepository,
                        BookingRepository bookingRepository) {
                this.userRepository = userRepository;
                this.vehicleRepository = vehicleRepository;
                this.bookingRepository = bookingRepository;
        }

        public DashboardStatsDTO getOwnerStats(String ownerEmail) {
                // 1. Find owner by email
                userRepository.findByEmail(ownerEmail)
                                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

                // 2. Get all vehicles owned by this user
                List<Vehicle> vehicles = vehicleRepository.findByOwnerEmail(ownerEmail);

                // 3. Get all bookings (we'll filter by vehicle owner)
                List<Booking> allBookings = bookingRepository.findAll();

                // 4. Filter bookings for this owner's vehicles
                List<Long> vehicleIds = vehicles.stream()
                                .map(Vehicle::getId)
                                .toList();

                List<Booking> ownerBookings = allBookings.stream()
                                .filter(b -> vehicleIds.contains(b.getVehicle().getId()))
                                .toList();

                // 5. Calculate metrics
                Double totalRevenue = ownerBookings.stream()
                                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                                .mapToDouble(Booking::getTotalPrice)
                                .sum();

                Integer activeVehicles = vehicles.size();

                Integer pendingBookings = (int) ownerBookings.stream()
                                .filter(b -> "WAITING_PAYMENT".equals(b.getStatus()))
                                .count();

                Integer completedBookings = (int) ownerBookings.stream()
                                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                                .count();

                return new DashboardStatsDTO(
                                totalRevenue,
                                activeVehicles,
                                pendingBookings,
                                completedBookings);
        }

        public List<BookingDTO> getActiveBookings(String ownerEmail) {
                // 1. Validate owner exists
                userRepository.findByEmail(ownerEmail)
                                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

                // 2. Get all vehicles owned by this user
                List<Vehicle> vehicles = vehicleRepository.findByOwnerEmail(ownerEmail);

                // 3. Get all bookings
                List<Booking> allBookings = bookingRepository.findAll();

                // 4. Filter bookings for this owner's vehicles that are currently active
                // (date-based)
                List<Long> vehicleIds = vehicles.stream()
                                .map(Vehicle::getId)
                                .toList();

                LocalDate today = LocalDate.now();

                List<Booking> filtered = allBookings.stream()
                                .filter(b -> vehicleIds.contains(b.getVehicle().getId()))
                                .filter(b -> !b.getReturnDate().isBefore(today)) // Show bookings that haven't ended yet
                                .toList();

                return filtered.stream()
                                .map(b -> new BookingDTO(
                                                b.getId(),
                                                b.getPickupDate(), // This maps to startDate in DTO
                                                b.getReturnDate(), // This maps to endDate in DTO
                                                b.getStatus(),
                                                b.getTotalPrice(),
                                                b.getVehicle().getId(),
                                                b.getRenter() != null ? b.getRenter().getId() : null)) // Handle null
                                                                                                       // renter
                                .toList();
        }
}
