package tqs.backend.payment;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @PostMapping("/{bookingId}")
    public void processPayment(@PathVariable Long bookingId) {
        // TODO SCRUM-16: simulate payment processing
    }
}
