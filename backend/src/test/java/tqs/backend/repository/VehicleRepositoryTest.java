package tqs.backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import tqs.backend.model.Booking;
import tqs.backend.model.Vehicle;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class VehicleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle fiat;
    private Vehicle tesla;

    @BeforeEach
    void setUp() {
        // 1. Criar Veículos
        fiat = new Vehicle(null, "Fiat", "500", 2021, "Citadino", "AA-00-AA", 10000, 
                "Gasolina", "Manual", 4, 3, true, true, true, 
                "Lisboa", "Aeroporto", 35.0, "Desc", "url");
        
        tesla = new Vehicle(null, "Tesla", "Model 3", 2023, "Sedan", "BB-11-BB", 5000, 
                "Elétrico", "Automática", 5, 5, true, true, true, 
                "Porto", "Campanhã", 85.0, "Desc", "url");

        entityManager.persist(fiat);
        entityManager.persist(tesla);

        // 2. Criar uma Reserva no Fiat para o Natal (20 a 25 Dezembro 2025)
        Booking booking = new Booking(null, 
                LocalDate.of(2025, 12, 20), 
                LocalDate.of(2025, 12, 25), 
                fiat);
        entityManager.persist(booking);
        
        entityManager.flush();
    }

    @Test
    @Requirement("SCRUM-49")
    void whenSearchByCity_thenReturnCorrectVehicles() {
        List<Vehicle> found = vehicleRepository.findByCityContainingIgnoreCase("Lisboa");
        
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getBrand()).isEqualTo("Fiat");
    }

    @Test
    @Requirement("SCRUM-49")
    void whenSearchAvailability_andDatesDoNotOverlap_thenReturnVehicle() {
        // Pesquisa para datas ANTES da reserva (10 a 15 Dezembro)
        List<Vehicle> available = vehicleRepository.findAvailableVehicles(
            "Lisboa", 
            LocalDate.of(2025, 12, 10), 
            LocalDate.of(2025, 12, 15)
        );

        assertThat(available).hasSize(1);
        assertThat(available.get(0).getBrand()).isEqualTo("Fiat");
    }

    @Test
    @Requirement("SCRUM-49")
    void whenSearchAvailability_andDatesOverlap_thenReturnEmpty() {
        // Pesquisa COLIDE com a reserva (22 a 23 Dezembro)
        List<Vehicle> available = vehicleRepository.findAvailableVehicles(
            "Lisboa", 
            LocalDate.of(2025, 12, 22), 
            LocalDate.of(2025, 12, 23)
        );

        assertThat(available).isEmpty();
    }
}