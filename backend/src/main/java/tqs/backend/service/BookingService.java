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
        public BookingDTO createBooking(BookingRequestDTO request) {
        // 0) Validate Dates first (fast-fail)
        if (request.getStartDate() == null || request.getEndDate() == null) {
                throw new IllegalArgumentException("Start date and end date are required");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new IllegalArgumentException("Start date must be before end date");
        }

        // 1) Validate Vehicle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        // 2) Validate User (by email or id)
        User renter;
        if (request.getRenterEmail() != null) {
                renter = userRepository.findByEmail(request.getRenterEmail())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } else if (request.getRenterId() != null) {
                renter = userRepository.findById(request.getRenterId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } else {
                throw new IllegalArgumentException("Renter information is required");
        }

        // 3) Check Availability (overlap)
        long overlapping = bookingRepository.countOverlappingBookings(
                request.getVehicleId(), request.getStartDate(), request.getEndDate());

        if (overlapping > 0) {
                throw new IllegalStateException("Vehicle is already booked for these dates");
        }

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days <= 0) days = 1;

        java.math.BigDecimal totalPrice = vehicle.getPricePerDay().multiply(java.math.BigDecimal.valueOf(days));

        java.time.OffsetDateTime startDateTime = request.getStartDate()
                .atStartOfDay()
                .atOffset(java.time.ZoneOffset.UTC);
        java.time.OffsetDateTime endDateTime = request.getEndDate()
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

        public BookingDTO confirmPayment(Long bookingId) {
                Booking booking = bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

                // Antes: "WAITING_PAYMENT"
                if (!"PENDING".equals(booking.getStatus())) {
                        throw new IllegalStateException(
                                "Booking is not waiting for payment. Current status: " + booking.getStatus()
                        );
                }

                booking.setStatus("CONFIRMED");
                Booking saved = bookingRepository.save(booking);
                return toBookingDTO(saved);
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
