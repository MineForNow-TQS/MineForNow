package tqs.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tqs.backend.dto.BookingDTO;
import tqs.backend.dto.BookingRequestDTO;
import tqs.backend.dto.PaymentDTO;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookingService {

        private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
        private static final String USER_NOT_FOUND = "User not found";

        private final BookingRepository bookingRepository;
        private final VehicleRepository vehicleRepository;
        private final UserRepository userRepository;

        public BookingService(BookingRepository bookingRepository, VehicleRepository vehicleRepository,
                        UserRepository userRepository) {
                this.bookingRepository = bookingRepository;
                this.vehicleRepository = vehicleRepository;
                this.userRepository = userRepository;
        }

        @SuppressWarnings("null")
        @Transactional
        public BookingDTO createBooking(BookingRequestDTO request) {
                // 1. Validate Vehicle
                Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

                // 2. Validate User (get from email if renterId not provided)
                User renter;
                if (request.getRenterEmail() != null) {
                        renter = userRepository.findByEmail(request.getRenterEmail())
                                        .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
                } else if (request.getRenterId() != null) {
                        renter = userRepository.findById(request.getRenterId())
                                        .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
                } else {
                        throw new IllegalArgumentException("Renter information is required");
                }

                // 3. Validate Dates
                if (request.getStartDate().isAfter(request.getEndDate())) {
                        throw new IllegalArgumentException("Start date must be before end date");
                }

                // 4. Check Availability
                long overlapping = bookingRepository.countOverlappingBookings(
                                request.getVehicleId(), request.getStartDate(), request.getEndDate());

                if (overlapping > 0) {
                        throw new IllegalStateException("Vehicle is already booked for these dates");
                }

                // 5. Calculate Price
                // Consider rental as inclusive of start and end date for simplicity, or day
                // difference
                long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
                if (days == 0)
                        days = 1; // Minimum 1 day

                // Ensure price is not negative
                if (days < 0)
                        throw new IllegalArgumentException("Invalid dates for price calculation");

                Double totalPrice = days * vehicle.getPricePerDay();

                // 6. Save Booking
                Booking booking = new Booking(
                                request.getStartDate(),
                                request.getEndDate(),
                                vehicle,
                                renter,
                                "WAITING_PAYMENT",
                                totalPrice);

                Booking saved = bookingRepository.save(booking);

                return new BookingDTO(
                                saved.getId(),
                                saved.getPickupDate(),
                                saved.getReturnDate(),
                                saved.getStatus(),
                                saved.getTotalPrice(),
                                saved.getVehicle().getId(),
                                saved.getRenter().getId());
        }

        @Transactional
        public BookingDTO confirmPayment(Long bookingId, PaymentDTO paymentData) {
                // 1. Validate Booking exists
                @SuppressWarnings("null")
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

                // 2. Validate status is WAITING_PAYMENT
                if (!"WAITING_PAYMENT".equals(booking.getStatus())) {
                        throw new IllegalStateException(
                                        "Booking is not waiting for payment. Current status: " + booking.getStatus());
                }

                // 3. Mock payment processing (always succeeds for MVP)
                logger.info("Processing payment for booking {}", bookingId);

                // 4. Update booking status
                booking.setStatus("CONFIRMED");
                booking.setPaymentDate(LocalDateTime.now());
                booking.setPaymentMethod("CREDIT_CARD");

                Booking confirmed = bookingRepository.save(booking);

                // 5. Simulate email confirmation (log only)
                logger.info("=== EMAIL CONFIRMATION (SIMULATED) ===");
                logger.info("Subject: Booking Confirmation #{}", confirmed.getId());
                logger.info("Your booking for {} {} has been confirmed!",
                                confirmed.getVehicle().getBrand(), confirmed.getVehicle().getModel());
                logger.info("Pickup: {}, Return: {}", confirmed.getPickupDate(), confirmed.getReturnDate());
                logger.info("Total: €{}", confirmed.getTotalPrice());
                logger.info("======================================");

                return new BookingDTO(
                                confirmed.getId(),
                                confirmed.getPickupDate(),
                                confirmed.getReturnDate(),
                                confirmed.getStatus(),
                                confirmed.getTotalPrice(),
                                confirmed.getVehicle().getId(),
                                confirmed.getRenter().getId());
        }

        public List<BookingDTO> getBookingsByUserEmail(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

                List<Booking> bookings = bookingRepository.findByRenter(user);

                return bookings.stream()
                                .map(booking -> new BookingDTO(
                                                booking.getId(),
                                                booking.getPickupDate(),
                                                booking.getReturnDate(),
                                                booking.getStatus(),
                                                booking.getTotalPrice(),
                                                booking.getVehicle().getId(),
                                                booking.getRenter().getId()))
                                .toList();
        }
}
