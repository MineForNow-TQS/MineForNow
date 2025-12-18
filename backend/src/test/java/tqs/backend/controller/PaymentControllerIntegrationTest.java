package tqs.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import tqs.backend.dto.BookingDTO;
import tqs.backend.dto.PaymentDTO;
import tqs.backend.service.BookingService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private BookingDTO confirmedBooking;
    private BookingDTO waitingBooking;

    @BeforeEach
    void setUp() {
        confirmedBooking = new BookingDTO(
                1L,
                LocalDate.of(2025, 12, 19),
                LocalDate.of(2025, 12, 23),
                "CONFIRMED",
                500.0,
                1L,
                1L);

        waitingBooking = new BookingDTO(
                2L,
                LocalDate.of(2025, 12, 25),
                LocalDate.of(2025, 12, 30),
                "WAITING_PAYMENT",
                600.0,
                2L,
                1L);
    }

    @Test
    @WithMockUser(username = "maria@email.com", roles = "RENTER")
    void confirmPayment_Success() throws Exception {
        when(bookingService.confirmPayment(eq(1L), any(PaymentDTO.class)))
                .thenReturn(confirmedBooking);

        mockMvc.perform(post("/api/bookings/1/confirm-payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "cardLast4Digits": "1234",
                            "cardholderName": "Maria Silva",
                            "expiryDate": "12/25",
                            "cvv": "123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(username = "maria@email.com", roles = "RENTER")
    void confirmPayment_BookingNotFound() throws Exception {
        when(bookingService.confirmPayment(eq(999L), any(PaymentDTO.class)))
                .thenThrow(new IllegalArgumentException("Booking not found"));

        mockMvc.perform(post("/api/bookings/999/confirm-payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "cardLast4Digits": "1234",
                            "cardholderName": "Maria Silva",
                            "expiryDate": "12/25",
                            "cvv": "123"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Booking not found"));
    }

    @Test
    @WithMockUser(username = "maria@email.com", roles = "RENTER")
    void confirmPayment_InvalidStatus() throws Exception {
        when(bookingService.confirmPayment(eq(1L), any(PaymentDTO.class)))
                .thenThrow(new IllegalStateException("Booking is not waiting for payment"));

        mockMvc.perform(post("/api/bookings/1/confirm-payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "cardLast4Digits": "1234",
                            "cardholderName": "Maria Silva",
                            "expiryDate": "12/25",
                            "cvv": "123"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Booking is not waiting for payment"));
    }

    @Test
    @WithMockUser(username = "maria@email.com", roles = "RENTER")
    void confirmPayment_InvalidCardFormat() throws Exception {
        mockMvc.perform(post("/api/bookings/1/confirm-payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "cardLast4Digits": "123",
                            "cardholderName": "Maria Silva",
                            "expiryDate": "12/25",
                            "cvv": "123"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmPayment_Unauthenticated() throws Exception {
        mockMvc.perform(post("/api/bookings/1/confirm-payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "cardLast4Digits": "1234",
                            "cardholderName": "Maria Silva",
                            "expiryDate": "12/25",
                            "cvv": "123"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }
}
