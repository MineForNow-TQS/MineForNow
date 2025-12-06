package tqs.backend.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleTest {

    @Test
    void testVehicleBuilder() {
        Vehicle vehicle = Vehicle.builder()
                .brand("Toyota")
                .model("Corolla")
                .year(2023)
                .type("Sedan")
                .licensePlate("AA-00-BB")
                .mileage(15000)
                .fuelType("Hybrid")
                .transmission("Automatic")
                .seats(5)
                .doors(4)
                .hasAC(true)
                .hasGPS(true)
                .hasBluetooth(true)
                .city("Lisboa")
                .exactLocation("Aeroporto")
                .pricePerDay(45.0)
                .description("Carro económico e confiável")
                .imageUrl("https://example.com/corolla.jpg")
                .build();

        assertThat(vehicle.getBrand()).isEqualTo("Toyota");
        assertThat(vehicle.getModel()).isEqualTo("Corolla");
        assertThat(vehicle.getYear()).isEqualTo(2023);
        assertThat(vehicle.getType()).isEqualTo("Sedan");
        assertThat(vehicle.getLicensePlate()).isEqualTo("AA-00-BB");
        assertThat(vehicle.getMileage()).isEqualTo(15000);
        assertThat(vehicle.getFuelType()).isEqualTo("Hybrid");
        assertThat(vehicle.getTransmission()).isEqualTo("Automatic");
        assertThat(vehicle.getSeats()).isEqualTo(5);
        assertThat(vehicle.getDoors()).isEqualTo(4);
        assertThat(vehicle.getHasAC()).isTrue();
        assertThat(vehicle.getHasGPS()).isTrue();
        assertThat(vehicle.getHasBluetooth()).isTrue();
        assertThat(vehicle.getCity()).isEqualTo("Lisboa");
        assertThat(vehicle.getExactLocation()).isEqualTo("Aeroporto");
        assertThat(vehicle.getPricePerDay()).isEqualTo(45.0);
        assertThat(vehicle.getDescription()).isEqualTo("Carro económico e confiável");
        assertThat(vehicle.getImageUrl()).isEqualTo("https://example.com/corolla.jpg");
    }

    @Test
    void testVehicleSetters() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setBrand("Honda");
        vehicle.setModel("Civic");
        vehicle.setYear(2022);
        vehicle.setType("Sedan");
        vehicle.setLicensePlate("BB-11-CC");
        vehicle.setMileage(25000);
        vehicle.setFuelType("Gasoline");
        vehicle.setTransmission("Manual");
        vehicle.setSeats(5);
        vehicle.setDoors(4);
        vehicle.setHasAC(false);
        vehicle.setHasGPS(false);
        vehicle.setHasBluetooth(true);
        vehicle.setCity("Porto");
        vehicle.setExactLocation("Centro");
        vehicle.setPricePerDay(40.0);
        vehicle.setDescription("Carro desportivo");
        vehicle.setImageUrl("https://example.com/civic.jpg");

        assertThat(vehicle.getId()).isEqualTo(1L);
        assertThat(vehicle.getBrand()).isEqualTo("Honda");
        assertThat(vehicle.getModel()).isEqualTo("Civic");
        assertThat(vehicle.getYear()).isEqualTo(2022);
        assertThat(vehicle.getType()).isEqualTo("Sedan");
        assertThat(vehicle.getLicensePlate()).isEqualTo("BB-11-CC");
        assertThat(vehicle.getMileage()).isEqualTo(25000);
        assertThat(vehicle.getFuelType()).isEqualTo("Gasoline");
        assertThat(vehicle.getTransmission()).isEqualTo("Manual");
        assertThat(vehicle.getSeats()).isEqualTo(5);
        assertThat(vehicle.getDoors()).isEqualTo(4);
        assertThat(vehicle.getHasAC()).isFalse();
        assertThat(vehicle.getHasGPS()).isFalse();
        assertThat(vehicle.getHasBluetooth()).isTrue();
        assertThat(vehicle.getCity()).isEqualTo("Porto");
        assertThat(vehicle.getExactLocation()).isEqualTo("Centro");
        assertThat(vehicle.getPricePerDay()).isEqualTo(40.0);
        assertThat(vehicle.getDescription()).isEqualTo("Carro desportivo");
        assertThat(vehicle.getImageUrl()).isEqualTo("https://example.com/civic.jpg");
    }

    @Test
    void testVehicleEqualsAndHashCode() {
        Vehicle vehicle1 = Vehicle.builder()
                .id(1L)
                .brand("BMW")
                .model("X5")
                .year(2023)
                .pricePerDay(100.0)
                .build();

        Vehicle vehicle2 = Vehicle.builder()
                .id(1L)
                .brand("BMW")
                .model("X5")
                .year(2023)
                .pricePerDay(100.0)
                .build();

        Vehicle vehicle3 = Vehicle.builder()
                .id(2L)
                .brand("Audi")
                .model("Q7")
                .year(2023)
                .pricePerDay(110.0)
                .build();

        assertThat(vehicle1).isEqualTo(vehicle2);
        assertThat(vehicle1).isNotEqualTo(vehicle3);
        assertThat(vehicle1.hashCode()).isEqualTo(vehicle2.hashCode());
    }

    @Test
    void testVehicleToString() {
        Vehicle vehicle = Vehicle.builder()
                .brand("Mercedes")
                .model("C-Class")
                .build();

        String toString = vehicle.toString();
        assertThat(toString).contains("Mercedes");
        assertThat(toString).contains("C-Class");
    }

    @Test
    void testVehicleNoArgsConstructor() {
        Vehicle vehicle = new Vehicle();
        assertThat(vehicle).isNotNull();
        assertThat(vehicle.getBrand()).isNull();
        assertThat(vehicle.getModel()).isNull();
    }

    @Test
    void testVehicleAllArgsConstructor() {
        Vehicle vehicle = new Vehicle(
                1L, "Porsche", "911", 2024, "Sports", "CC-22-DD", 5000,
                "Gasoline", "Automatic", 2, 2, true, true, true,
                "Lisboa", "Cascais", 200.0, "Carro desportivo de luxo",
                "https://example.com/911.jpg"
        );

        assertThat(vehicle.getId()).isEqualTo(1L);
        assertThat(vehicle.getBrand()).isEqualTo("Porsche");
        assertThat(vehicle.getModel()).isEqualTo("911");
        assertThat(vehicle.getPricePerDay()).isEqualTo(200.0);
    }
}
