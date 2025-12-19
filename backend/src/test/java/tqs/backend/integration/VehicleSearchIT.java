package tqs.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import tqs.backend.model.Vehicle;
import tqs.backend.model.User;
import tqs.backend.model.Booking;
import tqs.backend.repository.VehicleRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.ReviewRepository;
import tqs.backend.model.UserRole;
import java.util.Objects;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class VehicleSearchIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        // Limpar e preparar dados determinísticos para os testes de integração
        bookingRepository.deleteAll();
        reviewRepository.deleteAll();
                vehicleRepository.deleteAll();
        userRepository.deleteAll();

        User owner = userRepository.save(Objects.requireNonNull(User.builder()
                .email("owner@test.com")
                .fullName("Owner Test")
                .password("owner")
                .role(UserRole.OWNER)
                .build()));

        Vehicle mercedes = new Vehicle(null, owner, "Mercedes-Benz", "AMG GT", 2021, "Desportivo",
                "DD-04-DD", 18000, "Gasolina", "Automática", 2, 2, true, true, true,
                "Lisboa", "Avenida da Liberdade", 850.0,
                "Mercedes-AMG GT de luxo.", "/Images/photo-1617814076367-b759c7d7e738.jpeg");

        Vehicle ferrari = new Vehicle(null, owner, "Ferrari", "Roma", 2024, "Desportivo",
                "EE-05-EE", 1000, "Gasolina", "Automática", 2, 2, true, true, true,
                "Lisboa", "Parque das Nações", 950.0,
                "Ferrari Roma desportivo de luxo.", "/Images/photo-1606220838315-056192d5e927.jpeg");

        vehicleRepository.save(mercedes);
        vehicleRepository.save(ferrari);

        // Criar reserva para o Mercedes relativa a hoje
        var today = LocalDate.now();
        var pickup = today.plusDays(10);
        var dropoff = today.plusDays(15);
        bookingRepository.save(new Booking(null, pickup, dropoff, mercedes));
    }

    @Test
    @Requirement("SCRUM-49")
    void whenSearchLisbon_thenReturnsMercedesAndFerrari() {
        // Teste real contra a BD populada pelo MinefornowApplication
        String url = "http://localhost:" + randomServerPort + "/api/vehicles/search?city=Lisboa";

        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Vehicle>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Vehicle> cars = response.getBody();

        // Em Lisboa temos o Mercedes e o Ferrari (dados do MinefornowApplication)
        assertThat(cars).extracting(Vehicle::getBrand)
                .contains("Mercedes-Benz", "Ferrari");
    }

    @Test
    @Requirement("SCRUM-49")
    void whenSearchLisbonWithConflictDates_thenMercedesIsMissing() {
        // Sabemos que o Mercedes tem reserva criada no MinefornowApplication para:
        // Hoje + 10 dias até Hoje + 15 dias.

        LocalDate today = LocalDate.now();
        LocalDate pickup = today.plusDays(10);
        LocalDate dropoff = today.plusDays(12);

        String url = String.format("http://localhost:%d/api/vehicles/search?city=Lisboa&pickup=%s&dropoff=%s",
                randomServerPort, pickup, dropoff);

        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Vehicle>>() {
                });

        List<Vehicle> cars = response.getBody();

        // O Mercedes deve estar ausente porque está reservado!
        assertThat(cars).extracting(Vehicle::getBrand)
                .doesNotContain("Mercedes-Benz")
                .contains("Ferrari");
    }
}