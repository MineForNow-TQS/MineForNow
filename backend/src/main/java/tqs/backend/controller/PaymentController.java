package tqs.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.backend.dto.BookingDTO;
import tqs.backend.dto.PaymentDTO;
import tqs.backend.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class PaymentController {

    private final BookingService bookingService;

    public PaymentController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/{id}/confirm-payment")
    public ResponseEntity<Object> confirmPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentDTO paymentData) {
        try {
            BookingDTO confirmed = bookingService.confirmPayment(id, paymentData);
            return ResponseEntity.ok(confirmed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
