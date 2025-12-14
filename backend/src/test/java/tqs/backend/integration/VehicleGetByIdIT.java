package tqs.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.util.Objects;

/**
 * Testes de integração para o endpoint GET /api/vehicles/{id} (SCRUM-12).
 * Testa com banco de dados H2 real.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Vehicle GetById Integration Tests")
class VehicleGetByIdIT {

        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private VehicleRepository vehicleRepository;

        @Autowired
        private BookingRepository bookingRepository;

        @Autowired
        private UserRepository userRepository;

        private String baseUrl;
        private Vehicle savedVehicle;
        private User testOwner;

        @BeforeEach
        void setUp() {
                baseUrl = "http://localhost:" + port + "/api/vehicles";
                // Remover reservas primeiro para evitar violação de FK ao apagar veículos
                bookingRepository.deleteAll();
                vehicleRepository.deleteAll();
                userRepository.deleteAll();

                // Criar owner de teste
                testOwner = userRepository.save(Objects.requireNonNull(User.builder()
                                .email("testowner@test.com")
                                .fullName("Test Owner")
                                .password("test123")
                                .role(UserRole.OWNER)
                                .build()));

                // Criar veículo de teste no banco
                savedVehicle = Vehicle.builder()
                                .owner(testOwner)
                                .brand("Mercedes")
                                .model("Classe A")
                                .year(2022)
                                .type("Berlina")
                                .pricePerDay(45.0)
                                .city("Porto")
                                .exactLocation("Aeroporto Francisco Sá Carneiro")
                                .seats(5)
                                .doors(4)
                                .transmission("Automática")
                                .fuelType("Diesel")
                                .mileage(15000)
                                .hasAC(true)
                                .hasGPS(true)
                                .hasBluetooth(true)
                                .description("Mercedes Classe A em excelente estado")
                                .imageUrl("https://example.com/mercedes-a.jpg")
                                .licensePlate("AA-00-BB")
                                .build();

                savedVehicle = vehicleRepository.save(Objects.requireNonNull(savedVehicle));
        }

        @Test
        @Requirement("SCRUM-12")
        @DisplayName("GET /{id} - Deve retornar veículo completo com status 200")
        void whenGetExistingVehicle_thenReturns200WithFullData() {
                // Act
                ResponseEntity<VehicleDetailDTO> response = restTemplate.getForEntity(
                                baseUrl + "/" + savedVehicle.getId(),
                                VehicleDetailDTO.class);

                // Assert
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isNotNull();

                VehicleDetailDTO dto = response.getBody();
                assertThat(Objects.requireNonNull(dto).getId()).isEqualTo(savedVehicle.getId());
                assertThat(dto.getBrand()).isEqualTo("Mercedes");
                assertThat(dto.getModel()).isEqualTo("Classe A");
                assertThat(dto.getYear()).isEqualTo(2022);
                assertThat(dto.getPricePerDay()).isEqualTo(45.0);
                assertThat(dto.getCity()).isEqualTo("Porto");
                assertThat(dto.getSeats()).isEqualTo(5);
                assertThat(dto.getTransmission()).isEqualTo("Automática");
                assertThat(dto.getHasAC()).isTrue();
                assertThat(dto.getHasGPS()).isTrue();
                assertThat(dto.getDisplayName()).isEqualTo("Mercedes Classe A 2022");
                assertThat(dto.getFormattedPrice()).isEqualTo("45.00 €/dia");
        }

        @Test
        @Requirement("SCRUM-12")
        @DisplayName("GET /{id} - Deve retornar 404 para ID inexistente")
        void whenGetNonExistingVehicle_thenReturns404() {
                // Act
                ResponseEntity<VehicleDetailDTO> response = restTemplate.getForEntity(
                                baseUrl + "/99999",
                                VehicleDetailDTO.class);

                // Assert
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Requirement("SCRUM-12")
        @DisplayName("GET /{id} - Deve retornar campos formatados corretamente")
        void whenGetVehicle_thenFormattedFieldsAreCorrect() {
                // Act
                ResponseEntity<VehicleDetailDTO> response = restTemplate.getForEntity(
                                baseUrl + "/" + savedVehicle.getId(),
                                VehicleDetailDTO.class);

                // Assert
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                VehicleDetailDTO dto = response.getBody();

                assertThat(Objects.requireNonNull(dto).getDisplayName())
                                .isNotEmpty()
                                .contains("Mercedes")
                                .contains("Classe A")
                                .contains("2022");

                assertThat(dto.getFormattedPrice())
                                .isNotEmpty()
                                .contains("45.00")
                                .contains("€/dia");
        }
}
