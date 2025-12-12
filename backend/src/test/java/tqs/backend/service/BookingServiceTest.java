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
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.time.LocalDate;
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

    @BeforeEach
    void setUp() {
        renter = User.builder().id(2L).name("Renter").build();
        vehicle = Vehicle.builder().id(1L).pricePerDay(100.0).brand("Audi").build();
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
        assertThat(result.getStatus()).isEqualTo("WAITING_PAYMENT");
        assertThat(result.getTotalPrice()).isEqualTo(300.0);
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

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @Requirement("SCRUM-15")
    void createBooking_InvalidDates_ThrowsException() {
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate end = LocalDate.now().plusDays(1); // End before start
        BookingRequestDTO request = new BookingRequestDTO(1L, start, end, 2L);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(userRepository.findById(2L)).thenReturn(Optional.of(renter));

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
