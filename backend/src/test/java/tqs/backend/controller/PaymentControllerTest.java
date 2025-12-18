package tqs.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private BookingDTO testBooking;
    private PaymentDTO paymentData;

    @BeforeEach
    void setUp() {
        testBooking = new BookingDTO(
            1L,
            LocalDate.of(2025, 12, 19),
            LocalDate.of(2025, 12, 23),
            "CONFIRMED",
            500.0,
            1L,
            1L
        );

        paymentData = new PaymentDTO();
        paymentData.setCardLast4Digits("1234");
        paymentData.setCardholderName("Maria Silva");
        paymentData.setExpiryDate("12/25");
        paymentData.setCvv("123");
    }

    @Test
    @WithMockUser(username = "maria@email.com", roles = "RENTER")
    void confirmPayment_ValidPayment_ReturnsConfirmedBooking() throws Exception {
        when(bookingService.confirmPayment(eq(1L), any(PaymentDTO.class)))
            .thenReturn(testBooking);

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
            .andExpect(jsonPath("$.status").value("CONFIRMED"))
            .andExpect(jsonPath("$.totalPrice").value(500.0));
    }

    @Test
    @WithMockUser(username = "maria@email.com", roles = "RENTER")
    void confirmPayment_InvalidBookingId_Returns404() throws Exception {
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
            .andExpect(jsonPath("$.message").value("Booking not found"));
    }

    @Test
    @WithMockUser(username = "maria@email.com", roles = "RENTER")
    void confirmPayment_InvalidStatus_Returns400() throws Exception {
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
            .andExpect(jsonPath("$.message").value("Booking is not waiting for payment"));
    }

    @Test
    @WithMockUser(username = "maria@email.com", roles = "RENTER")
    void confirmPayment_InvalidCardNumber_Returns400() throws Exception {
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
    @WithMockUser(username = "maria@email.com", roles = "RENTER")
    void confirmPayment_MissingFields_Returns400() throws Exception {
        mockMvc.perform(post("/api/bookings/1/confirm-payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "cardLast4Digits": "1234"
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "maria@email.com", roles = "RENTER")
    void getMyBookings_ReturnsUserBookings() throws Exception {
        List<BookingDTO> bookings = Arrays.asList(
            testBooking,
            new BookingDTO(2L, LocalDate.of(2025, 12, 25), LocalDate.of(2025, 12, 30),
                "WAITING_PAYMENT", 600.0, 2L, 1L)
        );

        when(bookingService.getBookingsByUser("maria@email.com"))
            .thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/my-bookings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].status").value("WAITING_PAYMENT"));
    }

    @Test
    @WithMockUser(username = "unknown@email.com", roles = "RENTER")
    void getMyBookings_UserNotFound_Returns404() throws Exception {
        when(bookingService.getBookingsByUser("unknown@email.com"))
            .thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(get("/api/bookings/my-bookings"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void confirmPayment_Unauthenticated_Returns401() throws Exception {
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

    @Test
    void getMyBookings_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/bookings/my-bookings"))
            .andExpect(status().isUnauthorized());
    }
}
