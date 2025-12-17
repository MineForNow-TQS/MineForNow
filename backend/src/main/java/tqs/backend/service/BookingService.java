package tqs.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tqs.backend.dto.BookingDTO;
import tqs.backend.dto.BookingRequestDTO;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.time.temporal.ChronoUnit;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BookingDTO createBooking(BookingRequestDTO request) {
        // 1. Validate Vehicle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        // 2. Validate User
        User renter = userRepository.findById(request.getRenterId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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
}
