package tqs.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import tqs.backend.model.Vehicle;
import tqs.backend.repository.VehicleRepository;

@SpringBootApplication
public class MinefornowApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinefornowApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(VehicleRepository repository) {
        return args -> {
            Vehicle fiat = Vehicle.builder()
                .brand("Fiat")
                .model("500")
                .year(2022)
                .type("Citadino")
                .licensePlate("AA-00-00")
                .mileage(15000)
                .fuelType("Gasolina")
                .transmission("Manual")
                .seats(4)
                .doors(3)
                .hasAC(true)
                .hasGPS(false)
                .hasBluetooth(true)
                .city("Lisboa")
                .exactLocation("Aeroporto de Lisboa")
                .pricePerDay(35.0)
                .description("Carro compacto ideal para a cidade.")
                .imageUrl("https://images.unsplash.com/photo-1549317661-bd32c8ce0db2")
                .build();
            repository.save(fiat);

            Vehicle tesla = Vehicle.builder()
                .brand("Tesla")
                .model("Model 3")
                .year(2023)
                .type("Sedan")
                .licensePlate("BB-11-11")
                .mileage(5000)
                .fuelType("Elétrico")
                .transmission("Automática")
                .seats(5)
                .doors(5)
                .hasAC(true)
                .hasGPS(true)
                .hasBluetooth(true)
                .city("Porto")
                .exactLocation("Estação de Campanhã")
                .pricePerDay(85.0)
                .description("Conforto e tecnologia de ponta.")
                .imageUrl("https://images.unsplash.com/photo-1560958089-b8a1929cea89")
                .build();
            repository.save(tesla);
        };
    }
}