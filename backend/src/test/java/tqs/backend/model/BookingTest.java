package tqs.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BookingTest {

    @Test
    void testBookingConstructorAndGetters() {
        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .brand("Tesla")
                .model("Model 3")
                .pricePerDay(80.0)
                .build();

        LocalDate pickup = LocalDate.of(2025, 12, 10);
        LocalDate returnDate = LocalDate.of(2025, 12, 15);

        Booking booking = new Booking(1L, pickup, returnDate, vehicle);

        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getPickupDate()).isEqualTo(pickup);
        assertThat(booking.getReturnDate()).isEqualTo(returnDate);
        assertThat(booking.getVehicle()).isEqualTo(vehicle);
    }

    @Test
    void testBookingSetters() {
        Booking booking = new Booking();
        
        Vehicle vehicle = Vehicle.builder()
                .id(2L)
                .brand("BMW")
                .model("i4")
                .build();

        LocalDate pickup = LocalDate.of(2025, 12, 20);
        LocalDate returnDate = LocalDate.of(2025, 12, 25);

        booking.setId(5L);
        booking.setPickupDate(pickup);
        booking.setReturnDate(returnDate);
        booking.setVehicle(vehicle);

        assertThat(booking.getId()).isEqualTo(5L);
        assertThat(booking.getPickupDate()).isEqualTo(pickup);
        assertThat(booking.getReturnDate()).isEqualTo(returnDate);
        assertThat(booking.getVehicle()).isEqualTo(vehicle);
        assertThat(booking.getVehicle().getBrand()).isEqualTo("BMW");
    }

    @Test
    void testBookingNoArgsConstructor() {
        Booking booking = new Booking();
        
        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isNull();
        assertThat(booking.getPickupDate()).isNull();
        assertThat(booking.getReturnDate()).isNull();
        assertThat(booking.getVehicle()).isNull();
    }

    @Test
    void testBookingEqualsAndHashCode() {
        Vehicle vehicle = Vehicle.builder().id(1L).brand("Audi").build();
        LocalDate pickup = LocalDate.of(2025, 12, 1);
        LocalDate returnDate = LocalDate.of(2025, 12, 5);

        Booking booking1 = new Booking(1L, pickup, returnDate, vehicle);
        Booking booking2 = new Booking(1L, pickup, returnDate, vehicle);
        Booking booking3 = new Booking(2L, pickup.plusDays(1), returnDate, vehicle);

        assertThat(booking1).isEqualTo(booking2);
        assertThat(booking1).isNotEqualTo(booking3);
        assertThat(booking1.hashCode()).isEqualTo(booking2.hashCode());
    }

    @Test
    void testBookingToString() {
        Vehicle vehicle = Vehicle.builder()
                .brand("Ford")
                .model("Mustang")
                .build();

        Booking booking = new Booking();
        booking.setPickupDate(LocalDate.of(2025, 12, 10));
        booking.setReturnDate(LocalDate.of(2025, 12, 15));
        booking.setVehicle(vehicle);

        String toString = booking.toString();
        assertThat(toString).contains("2025-12-10");
        assertThat(toString).contains("2025-12-15");
    }

    @Test
    void testBookingDateValidation() {
        Vehicle vehicle = Vehicle.builder().id(1L).build();
        LocalDate pickup = LocalDate.of(2025, 12, 10);
        LocalDate returnDate = LocalDate.of(2025, 12, 15);

        Booking booking = new Booking(null, pickup, returnDate, vehicle);

        assertThat(booking.getPickupDate()).isBefore(booking.getReturnDate());
        assertThat(booking.getReturnDate()).isAfter(booking.getPickupDate());
    }
}
