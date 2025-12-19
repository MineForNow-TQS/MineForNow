package tqs.backend.service;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.backend.dto.BookingDTO;
import tqs.backend.dto.BookingRequestDTO;
import tqs.backend.dto.PaymentDTO;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User renter;
    private Vehicle vehicle;

    LocalDate pickupDate = LocalDate.of(2025, 12, 19);
    LocalDate returnDate = LocalDate.of(2025, 12, 23);

    OffsetDateTime pickupDateTime =
            pickupDate.atStartOfDay().atOffset(ZoneOffset.UTC);

    OffsetDateTime returnDateTime =
            returnDate.atStartOfDay().atOffset(ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        renter = User.builder().id(2L).fullName("Renter").build();
        vehicle = Vehicle.builder().id(1L).pricePerDay(BigDecimal.valueOf(100.0)).brand("Audi").build();
    }

    @Test
    @Requirement("SCRUM-15")
    void createBooking_ValidRequest_ReturnsDTO() {
        // Rent for 3 days: 10, 11, 12 (difference between 10 and 12 is 2 days?? Wait.)
        // ChronoUnit.DAYS.between(10, 12) = 2.
        // 10->11 (1), 11->12 (1).
        // If I want 3 days inclusive, usually it's [start, end].
        // My implementation uses ChronoUnit.DAYS.between(start, end).
        // If start=10, end=13 -> 3 days. Result 300.
        // Let's assume input dates behave like this.

        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(3); // 3 days later
        BookingRequestDTO request = new BookingRequestDTO(1L, start, end, 2L);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(userRepository.findById(2L)).thenReturn(Optional.of(renter));
        when(bookingRepository.countOverlappingBookings(1L, start, end)).thenReturn(0L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(10L);
            return b;
        });

        BookingDTO result = bookingService.createBooking(request);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getTotalPrice()).isEqualByComparingTo(new BigDecimal("300.0"));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @Requirement("SCRUM-15")
    void createBooking_Overlap_ThrowsException() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);
        BookingRequestDTO request = new BookingRequestDTO(1L, start, end, 2L);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(userRepository.findById(2L)).thenReturn(Optional.of(renter));
        when(bookingRepository.countOverlappingBookings(1L, start, end)).thenReturn(1L);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already booked");

        verify(bookingRepository).countOverlappingBookings(1L, start, end);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @Requirement("SCRUM-15")
    void createBooking_InvalidDates_ThrowsException() {
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate end = LocalDate.now().plusDays(1); // End before start
        BookingRequestDTO request = new BookingRequestDTO(1L, start, end, 2L);

        // when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        // when(userRepository.findById(2L)).thenReturn(Optional.of(renter));

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // SCRUM-16: Payment Confirmation Tests

    @Test
    @Requirement("SCRUM-16")
    void confirmPayment_Success_UpdatesStatusToConfirmed() {
        // Given
        Long bookingId = 1L;

        Booking booking = new Booking(
                null,
                vehicle,
                renter,
                pickupDateTime,
                returnDateTime,
                "PENDING",                
                BigDecimal.valueOf(500.0),
                "EUR",                 
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        booking.setId(1L);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        BookingDTO result = bookingService.confirmPayment(bookingId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("CONFIRMED");
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @Requirement("SCRUM-16")
    void confirmPayment_BookingNotFound_ThrowsException() {
        // Given
        Long bookingId = 999L;
        PaymentDTO paymentData = new PaymentDTO("1234", "John Doe", "12/25", "123");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookingService.confirmPayment(bookingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking not found");

        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @Requirement("SCRUM-16")
    void confirmPayment_InvalidStatus_ThrowsException() {
        Booking booking = new Booking(
                null,
                vehicle,
                renter,
                pickupDateTime,
                returnDateTime,
                "CONFIRMED",
                BigDecimal.valueOf(500.0),
                "UNPAID",
                OffsetDateTime.now(ZoneOffset.UTC)
        );
        booking.setId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        PaymentDTO paymentData = new PaymentDTO("1234", "Maria Silva", "12/25", "123");

        assertThatThrownBy(() -> bookingService.confirmPayment(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Booking is not waiting for payment");
    }

    @Test
    @Requirement("SCRUM-16")
    void getBookingsByUser_Success() {
        renter.setEmail("maria@email.com");
        Booking booking1 = new Booking(
                null,
                vehicle,
                renter,
                pickupDateTime,
                returnDateTime,
                "CONFIRMED",
                BigDecimal.valueOf(500.0),
                "UNPAID",
                OffsetDateTime.now(ZoneOffset.UTC)
        );
        booking1.setId(1L);

        LocalDate pickupDate2 = LocalDate.of(2025, 12, 25);
        LocalDate returnDate2 = LocalDate.of(2025, 12, 30);

        Booking booking2 = new Booking(
                null,
                vehicle,
                renter,
                pickupDate2.atStartOfDay().atOffset(ZoneOffset.UTC),
                returnDate2.atStartOfDay().atOffset(ZoneOffset.UTC),
                "WAITING_PAYMENT",
                BigDecimal.valueOf(600.0),
                "UNPAID",
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        booking2.setId(2L);

        when(userRepository.findByEmail("maria@email.com")).thenReturn(Optional.of(renter));
        when(bookingRepository.findByRenter(renter)).thenReturn(java.util.Arrays.asList(booking1, booking2));

        var result = bookingService.getBookingsByUserEmail("maria@email.com");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo("CONFIRMED");
        assertThat(result.get(1).getStatus()).isEqualTo("WAITING_PAYMENT");
    }

    @Test
    @Requirement("SCRUM-16")
    void getBookingsByUser_UserNotFound() {
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingsByUserEmail("unknown@email.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    @Requirement("SCRUM-16")
    void getBookingsByUserEmail_Success() {
        renter.setEmail("maria@email.com");
                
        Booking booking = new Booking(
                null,
                vehicle,
                renter,
                pickupDate.atStartOfDay().atOffset(ZoneOffset.UTC),
                returnDate.atStartOfDay().atOffset(ZoneOffset.UTC),
                "CONFIRMED",               // ou o status que o teste espera
                BigDecimal.valueOf(500.0), // ajusta o valor conforme o teste
                "UNPAID",
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        booking.setId(1L);

        when(userRepository.findByEmail("maria@email.com")).thenReturn(Optional.of(renter));
        when(bookingRepository.findByRenter(renter)).thenReturn(java.util.Arrays.asList(booking));

        var result = bookingService.getBookingsByUserEmail("maria@email.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    @Requirement("SCRUM-16")
    void getBookingsByUserEmail_UserNotFound() {
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingsByUserEmail("unknown@email.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }
}
