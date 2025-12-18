package tqs.backend.service;

import java.time.LocalDate;
import java.util.List;

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
                System.out.println("=== Calculating metrics ===");
                System.out.println("Owner bookings count: " + ownerBookings.size());
                ownerBookings.forEach(b -> System.out.println("  Booking " + b.getId() + ": status=" + b.getStatus()
                                + ", price=" + b.getTotalPrice()));

                Double totalRevenue = ownerBookings.stream()
                                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                                .mapToDouble(Booking::getTotalPrice)
                                .sum();
                System.out.println("Total revenue (sum of CONFIRMED): " + totalRevenue);

                Integer activeVehicles = vehicles.size();

                Integer pendingBookings = (int) ownerBookings.stream()
                                .filter(b -> "WAITING_PAYMENT".equals(b.getStatus()))
                                .count();

                Integer completedBookings = (int) ownerBookings.stream()
                                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                                .count();
                System.out.println("Completed bookings count: " + completedBookings);

                return new DashboardStatsDTO(
                                totalRevenue,
                                activeVehicles,
                                pendingBookings,
                                completedBookings);
        }

        public List<BookingDTO> getActiveBookings(String ownerEmail) {
                System.out.println("=== DEBUG getActiveBookings for: " + ownerEmail);

                // 1. Find owner by email
                User owner = userRepository.findByEmail(ownerEmail)
                                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
                System.out.println("Owner found: " + owner.getId());

                // 2. Get all vehicles owned by this user
                List<Vehicle> vehicles = vehicleRepository.findByOwnerEmail(ownerEmail);
                System.out.println("Owner has " + vehicles.size() + " vehicles");

                // 3. Get all bookings
                List<Booking> allBookings = bookingRepository.findAll();
                System.out.println("Total bookings in DB: " + allBookings.size());

                // 4. Filter bookings for this owner's vehicles that are currently active
                // (date-based)
                List<Long> vehicleIds = vehicles.stream()
                                .map(Vehicle::getId)
                                .toList();
                System.out.println("Vehicle IDs: " + vehicleIds);

                LocalDate today = LocalDate.now();
                System.out.println("Today: " + today);

                // Log all bookings with their dates
                System.out.println("=== All owner bookings with dates ===");
                allBookings.stream()
                                .filter(b -> vehicleIds.contains(b.getVehicle().getId()))
                                .forEach(b -> System.out.println(
                                                "  Booking " + b.getId() + ": pickup=" + b.getPickupDate() + ", return="
                                                                + b.getReturnDate() + ", status=" + b.getStatus()));

                List<Booking> filtered = allBookings.stream()
                                .filter(b -> vehicleIds.contains(b.getVehicle().getId()))
                                .filter(b -> {
                                        // Show bookings that haven't ended yet (return date >= today)
                                        boolean isActiveOrFuture = !b.getReturnDate().isBefore(today);
                                        if (isActiveOrFuture) {
                                                System.out.println("  Active/Future booking " + b.getId() + ": "
                                                                + b.getPickupDate() + " to " + b.getReturnDate()
                                                                + " (status: " + b.getStatus() + ")");
                                        }
                                        return isActiveOrFuture;
                                })
                                .toList();

                System.out.println("Filtered active bookings (by date): " + filtered.size());

                return filtered.stream()
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
