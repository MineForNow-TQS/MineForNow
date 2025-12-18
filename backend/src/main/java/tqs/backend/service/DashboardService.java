package tqs.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.backend.dto.BookingDTO;
import tqs.backend.dto.DashboardStatsDTO;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.util.List;

@Service
public class DashboardService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private VehicleRepository vehicleRepository;

        @Autowired
        private BookingRepository bookingRepository;

        public DashboardStatsDTO getOwnerStats(String ownerEmail) {
                // 1. Find owner by email
                User owner = userRepository.findByEmail(ownerEmail)
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

        public List<BookingDTO> getPendingBookings(String ownerEmail) {
                // 1. Find owner by email
                User owner = userRepository.findByEmail(ownerEmail)
                                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

                // 2. Get all vehicles owned by this user
                List<Vehicle> vehicles = vehicleRepository.findByOwnerEmail(ownerEmail);

                // 3. Get all bookings
                List<Booking> allBookings = bookingRepository.findAll();

                // 4. Filter bookings for this owner's vehicles with WAITING_PAYMENT status
                List<Long> vehicleIds = vehicles.stream()
                                .map(Vehicle::getId)
                                .toList();

                return allBookings.stream()
                                .filter(b -> vehicleIds.contains(b.getVehicle().getId()))
                                .filter(b -> "WAITING_PAYMENT".equals(b.getStatus()))
                                .map(b -> new BookingDTO(
                                                b.getId(),
                                                b.getPickupDate(),
                                                b.getReturnDate(),
                                                b.getStatus(),
                                                b.getTotalPrice(),
                                                b.getVehicle().getId(),
                                                b.getRenter().getId()))
                                .toList();
        }
}
