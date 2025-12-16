package tqs.backend.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

class BookingTest {

    @Test
    void testBookingConstructorAndGetters() {
        User renter = User.builder()
                .id(1L)
                .email("renter@test.com")
                .fullName("Renter")
                .passwordHash("hash")
                .role(UserRole.RENTER)
                .build();

        Vehicle vehicle = Vehicle.builder()
                .id(10L)
            .title("Tesla Model 3")
            .brand("Tesla")
                .model("Model 3")
                .city("Porto")
                .pricePerDay(BigDecimal.valueOf(45))
                .build();

        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusDays(3);

        Booking booking = new Booking(
            100L,
            vehicle,
            renter,
            start,
            end,
            "CONFIRMED",
            BigDecimal.valueOf(135.00),
            "EUR",
            OffsetDateTime.now()
        );

        assertThat(booking.getId()).isEqualTo(100L);
        assertThat(booking.getVehicle()).isEqualTo(vehicle);
        assertThat(booking.getRenter()).isEqualTo(renter);
        assertThat(booking.getStartDateTime()).isEqualTo(start);
        assertThat(booking.getEndDateTime()).isEqualTo(end);
        assertThat(booking.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(135.00));
        assertThat(booking.getStatus()).isEqualTo("CONFIRMED");
        assertThat(booking.getCreatedAt()).isNotNull();
    }

    @Test
    void testSetters() {
        Booking booking = new Booking();
        OffsetDateTime s = OffsetDateTime.now().plusDays(2);
        OffsetDateTime e = s.plusDays(1);

        booking.setStartDateTime(s);
        booking.setEndDateTime(e);
        booking.setTotalPrice(BigDecimal.valueOf(50));
        booking.setStatus("PENDING");

        assertThat(booking.getStartDateTime()).isEqualTo(s);
        assertThat(booking.getEndDateTime()).isEqualTo(e);
        assertThat(booking.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(booking.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void testEqualsAndHashCode() {
        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusDays(2);

        Booking b1 = Booking.builder()
            .id(1L)
            .startDateTime(start)
            .endDateTime(end)
            .totalPrice(BigDecimal.valueOf(100))
            .status("CONFIRMED")
            .build();

        Booking b2 = Booking.builder()
            .id(1L)
            .startDateTime(start)
            .endDateTime(end)
            .totalPrice(BigDecimal.valueOf(100))
            .status("CONFIRMED")
            .build();

        assertThat(b1.getId()).isEqualTo(b2.getId());
    }

    @Test
    void testToStringNotNull() {
        Booking b = Booking.builder().id(1L).status("CONFIRMED").build();
        assertThat(b.toString()).isNotNull();
    }
}
