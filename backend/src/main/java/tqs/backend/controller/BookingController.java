package tqs.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tqs.backend.dto.BookingDTO;
import tqs.backend.dto.BookingRequestDTO;
import tqs.backend.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingRequestDTO bookingRequest) {
        try {
            // Get authenticated user email from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Set renter email in request (will be resolved to User in service)
            bookingRequest.setRenterEmail(userEmail);

            BookingDTO booking = bookingService.createBooking(bookingRequest);
            return new ResponseEntity<>(booking, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
