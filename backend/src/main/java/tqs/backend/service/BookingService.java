package tqs.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookingService {

        private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
        private static final String USER_NOT_FOUND = "User not found";

        @Autowired
        private BookingRepository bookingRepository;

        @Autowired
        private VehicleRepository vehicleRepository;

        @Autowired
        private UserRepository userRepository;

        @Transactional
        public BookingDTO createBooking(BookingRequestDTO bookingRequest) {
        Vehicle vehicle = vehicleRepository.findById(bookingRequest.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        User renter = userRepository.findByEmail(bookingRequest.getRenterEmail())
                .orElseThrow(() -> new RuntimeException("Renter not found"));

        long overlappingBookings = bookingRepository.countOverlappingBookings(
                vehicle.getId(),
                bookingRequest.getStartDate(),
                bookingRequest.getEndDate()
        );

        if (overlappingBookings > 0) {
                throw new RuntimeException("Vehicle is not available for the selected dates");
        }

        long days = ChronoUnit.DAYS.between(bookingRequest.getStartDate(), bookingRequest.getEndDate());
        if (days <= 0) days = 1;

        java.math.BigDecimal totalPrice = vehicle.getPricePerDay().multiply(java.math.BigDecimal.valueOf(days));

        java.time.OffsetDateTime startDateTime = bookingRequest.getStartDate()
                .atStartOfDay()
                .atOffset(java.time.ZoneOffset.UTC);
        java.time.OffsetDateTime endDateTime = bookingRequest.getEndDate()
                .atStartOfDay()
                .atOffset(java.time.ZoneOffset.UTC);

        Booking booking = new Booking(
                null,
                vehicle,
                renter,
                startDateTime,
                endDateTime,
                "PENDING",
                totalPrice,
                "EUR",
                java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)
        );

        Booking saved = bookingRepository.save(booking);

        return new BookingDTO(
                saved.getId(),
                saved.getPickupDate(),
                saved.getReturnDate(),
                saved.getVehicle().getId(),
                saved.getRenter().getId(),
                saved.getStatus(),
                saved.getTotalPrice(),
                saved.getCurrency(),
                saved.getCreatedAt()
        );
        }

        @Transactional
        public BookingDTO confirmPayment(Long bookingId, PaymentDTO paymentData) {
                // 1. Validate Booking exists
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

                return toBookingDTO(confirmed);
        }

        public List<BookingDTO> getBookingsByUserEmail(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

                List<Booking> bookings = bookingRepository.findByRenter(user);

                return bookings.stream()
                        .map(this::toBookingDTO)
                        .collect(Collectors.toList());

        }

        private BookingDTO toBookingDTO(Booking booking) {
                return new BookingDTO(
                        booking.getId(),
                        booking.getPickupDate(),
                        booking.getReturnDate(),        
                        booking.getVehicle().getId(),
                        booking.getRenter().getId(),
                        booking.getStatus(),
                        booking.getTotalPrice(),
                        booking.getCurrency(),
                        booking.getCreatedAt()
                );
        
        }
}
