package tqs.backend.integration;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import tqs.backend.dto.CreateVehicleRequest;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;
import tqs.backend.security.JwtUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração para criação de veículos (SCRUM-10).
 * Testa o fluxo completo: Controller -> Service -> Repository.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Vehicle Creation Integration Tests")
class VehicleControllerCreateIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private VehicleRepository vehicleRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private JwtUtils jwtUtils;

        private User ownerUser;
        private User renterUser;
        private String ownerToken;
        private String renterToken;
        private CreateVehicleRequest validRequest;

        @BeforeEach
        void setUp() {
                // Limpar repositórios
                vehicleRepository.deleteAll();
                userRepository.deleteAll();

                // Criar utilizador owner
                ownerUser = User.builder()
                                .email("owner@test.com")
                                .password(passwordEncoder.encode("password123"))
                                .fullName("Test Owner")
                                .role(UserRole.OWNER)
                                .build();
                ownerUser = userRepository.save(ownerUser);
                ownerToken = jwtUtils.generateJwtToken(ownerUser.getEmail());

                // Criar utilizador renter
                renterUser = User.builder()
                                .email("renter@test.com")
                                .password(passwordEncoder.encode("password123"))
                                .fullName("Test Renter")
                                .role(UserRole.RENTER)
                                .build();
                renterUser = userRepository.save(renterUser);
                renterToken = jwtUtils.generateJwtToken(renterUser.getEmail());

                // Criar request válido
                validRequest = new CreateVehicleRequest();
                validRequest.setBrand("Toyota");
                validRequest.setModel("Corolla");
                validRequest.setYear(2022);
                validRequest.setType("Sedan");
                validRequest.setPricePerDay(45.0);
                validRequest.setCity("Lisboa");
                validRequest.setFuelType("Híbrido");
                validRequest.setTransmission("Automático");
                validRequest.setSeats(5);
                validRequest.setDoors(4);
                validRequest.setHasAC(true);
                validRequest.setHasGPS(true);
                validRequest.setHasBluetooth(true);
                validRequest.setLicensePlate("AA-12-BB");
                validRequest.setMileage(25000);
                validRequest.setExactLocation("Estação de Santa Apolónia");
                validRequest.setDescription("Veículo confortável e económico");
        }

        @Test
        @Requirement("SCRUM-10")
        @DisplayName("Quando owner autenticado cria veículo válido, deve retornar 201 e veículo criado")
        void whenOwnerCreatesValidVehicle_thenReturn201() throws Exception {
                mockMvc.perform(post("/api/vehicles")
                                .header("Authorization", "Bearer " + ownerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.brand").value("Toyota"))
                                .andExpect(jsonPath("$.model").value("Corolla"))
                                .andExpect(jsonPath("$.year").value(2022))
                                .andExpect(jsonPath("$.pricePerDay").value(45.0))
                                .andExpect(jsonPath("$.city").value("Lisboa"));

                // Verificar que foi salvo na base de dados
                assertThat(vehicleRepository.findAll()).hasSize(1);
        }

        @Test
        @Requirement("SCRUM-10")
        @DisplayName("Quando renter tenta criar veículo, deve retornar 400")
        void whenRenterTriesToCreateVehicle_thenReturn400() throws Exception {
                mockMvc.perform(post("/api/vehicles")
                                .header("Authorization", "Bearer " + renterToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isBadRequest());

                // Verificar que NÃO foi salvo
                assertThat(vehicleRepository.findAll()).isEmpty();
        }

        @Test
        @Requirement("SCRUM-10")
        @DisplayName("Quando não autenticado tenta criar veículo, deve retornar 401")
        void whenUnauthenticatedTriesToCreateVehicle_thenReturn401() throws Exception {
                mockMvc.perform(post("/api/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isUnauthorized());

                // Verificar que NÃO foi salvo
                assertThat(vehicleRepository.findAll()).isEmpty();
        }

        @Test
        @Requirement("SCRUM-10")
        @DisplayName("Quando request inválido (sem marca), deve retornar 400")
        void whenInvalidRequest_thenReturn400() throws Exception {
                validRequest.setBrand(null); // Marca é obrigatória

                mockMvc.perform(post("/api/vehicles")
                                .header("Authorization", "Bearer " + ownerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isBadRequest());

                // Verificar que NÃO foi salvo
                assertThat(vehicleRepository.findAll()).isEmpty();
        }

        @Test
        @Requirement("SCRUM-10")
        @DisplayName("Quando request inválido (sem preço), deve retornar 400")
        void whenMissingPrice_thenReturn400() throws Exception {
                validRequest.setPricePerDay(null);

                mockMvc.perform(post("/api/vehicles")
                                .header("Authorization", "Bearer " + ownerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isBadRequest());

                // Verificar que NÃO foi salvo
                assertThat(vehicleRepository.findAll()).isEmpty();
        }

        @Test
        @Requirement("SCRUM-10")
        @DisplayName("Quando cria múltiplos veículos, todos devem ser associados ao owner")
        void whenCreatesMultipleVehicles_allBelongToOwner() throws Exception {
                // Criar primeiro veículo
                mockMvc.perform(post("/api/vehicles")
                                .header("Authorization", "Bearer " + ownerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isCreated());

                // Criar segundo veículo
                validRequest.setBrand("Honda");
                validRequest.setModel("Civic");
                validRequest.setLicensePlate("CC-34-DD"); // Different license plate
                mockMvc.perform(post("/api/vehicles")
                                .header("Authorization", "Bearer " + ownerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isCreated());

                // Verificar que ambos foram salvos
                assertThat(vehicleRepository.findAll()).hasSize(2);

                // Verificar que todos pertencem ao owner
                var vehicles = vehicleRepository.findByOwnerEmail(ownerUser.getEmail());
                assertThat(vehicles).hasSize(2);
        }
}
