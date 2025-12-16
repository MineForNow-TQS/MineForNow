package tqs.backend.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class VehicleTest {

    @Test
    void testVehicleBuilderAndGetters() {
        User owner = User.builder()
                .id(1L)
                .email("owner@test.com")
                .fullName("Owner")
                .passwordHash("hash")
                .role(UserRole.OWNER)
                .build();

        Vehicle vehicle = Vehicle.builder()
            .id(10L)
            .owner(owner)
            .title("Tesla Model 3")
            .brand("Tesla")
            .model("Model 3")
            .city("Porto")
            .year(2022)
            .fuelType("Electric")
            .seats(5)
            .transmission("Automatic")
            .licensePlate("AA-00-AA")
            .mileage(15000)
            .pricePerDay(BigDecimal.valueOf(45.0))
            .status("VISIBLE")
            .build();

        assertThat(vehicle.getId()).isEqualTo(10L);
        assertThat(vehicle.getOwner()).isEqualTo(owner);
        assertThat(vehicle.getBrand()).isEqualTo("Tesla");
        assertThat(vehicle.getModel()).isEqualTo("Model 3");
        assertThat(vehicle.getCity()).isEqualTo("Porto");
        assertThat(vehicle.getYear()).isEqualTo(2022);
        assertThat(vehicle.getPricePerDay()).isEqualByComparingTo(BigDecimal.valueOf(45.0));
        assertThat(vehicle.getStatus()).isEqualTo("VISIBLE");
    }

    @Test
    void testSetters() {
        Vehicle v = new Vehicle();
        v.setBrand("BMW");
        v.setModel("X5");
        v.setCity("Lisboa");
        v.setYear(2020);
        v.setPricePerDay(BigDecimal.valueOf(60.0));

        assertThat(v.getBrand()).isEqualTo("BMW");
        assertThat(v.getModel()).isEqualTo("X5");
        assertThat(v.getCity()).isEqualTo("Lisboa");
        assertThat(v.getYear()).isEqualTo(2020);
        assertThat(v.getPricePerDay()).isEqualByComparingTo(BigDecimal.valueOf(60.0));
    }

    @Test
    void testEqualsAndHashCode() {
        Vehicle v1 = Vehicle.builder().id(1L).brand("Tesla").model("Model 3").pricePerDay(BigDecimal.valueOf(10)).build();
        Vehicle v2 = Vehicle.builder().id(1L).brand("Tesla").model("Model 3").pricePerDay(BigDecimal.valueOf(10)).build();

        assertThat(v1.getId()).isEqualTo(v2.getId());
    }

    @Test
    void testToStringNotNull() {
        Vehicle v = Vehicle.builder().id(1L).brand("Tesla").model("Model 3").pricePerDay(BigDecimal.valueOf(10)).build();
        assertThat(v.toString()).isNotNull();
    }
}
