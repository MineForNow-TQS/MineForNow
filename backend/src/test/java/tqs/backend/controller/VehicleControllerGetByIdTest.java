package tqs.backend.controller;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;
import tqs.backend.service.VehicleService;


import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;


/**
 * Testes unitários do VehicleController - Endpoint GET /{id} (SCRUM-12).
 */
@WebMvcTest(VehicleController.class)
@DisplayName("VehicleController GET /{id} Tests")
@WithMockUser 
class VehicleControllerGetByIdTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleRepository vehicleRepository;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    private VehicleDetailDTO testVehicleDTO;

    @BeforeEach
    void setUp() {
        testVehicleDTO = VehicleDetailDTO.builder()
                .id(1L)
                .brand("Fiat")
                .model("500")
                .year(2020)
                .type("Citadino")
                .pricePerDay(25.0)
                .city("Lisboa")
                .seats(4)
                .doors(3)
                .transmission("Manual")
                .fuelType("Gasolina")
                .hasAC(true)
                .hasGPS(false)
                .hasBluetooth(true)
                .description("Carro económico perfeito para cidade")
                .imageUrl("https://example.com/fiat500.jpg")
                .displayName("Fiat 500 2020")
                .formattedPrice("25.00 €/dia")
                .build();
    }

    @Test
    @Requirement("SCRUM-12")
    @DisplayName("GET /api/vehicles/{id} - Deve retornar 200 OK com dados corretos quando veículo existe")
    void givenExistingVehicle_whenGetById_thenReturns200WithCorrectData() throws Exception {
        // Arrange
        when(vehicleService.getVehicleById(1L)).thenReturn(Optional.of(testVehicleDTO));

        // Act & Assert
        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brand").value("Fiat"))
                .andExpect(jsonPath("$.model").value("500"))
                .andExpect(jsonPath("$.year").value(2020))
                .andExpect(jsonPath("$.pricePerDay").value(25.0))
                .andExpect(jsonPath("$.city").value("Lisboa"))
                .andExpect(jsonPath("$.displayName").value("Fiat 500 2020"))
                .andExpect(jsonPath("$.formattedPrice").value("25.00 €/dia"));
    }

    @Test
    @Requirement("SCRUM-12")
    @DisplayName("GET /api/vehicles/{id} - Deve retornar 404 Not Found quando veículo não existe")
    void givenNonExistingVehicle_whenGetById_thenReturns404() throws Exception {
        // Arrange
        when(vehicleService.getVehicleById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/vehicles/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Requirement("SCRUM-12")
    @DisplayName("GET /api/vehicles/{id} - Deve retornar todos os campos do DTO")
    void givenExistingVehicle_whenGetById_thenReturnsAllFields() throws Exception {
        // Arrange
        when(vehicleService.getVehicleById(1L)).thenReturn(Optional.of(testVehicleDTO));

        // Act & Assert
        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats").value(4))
                .andExpect(jsonPath("$.doors").value(3))
                .andExpect(jsonPath("$.transmission").value("Manual"))
                .andExpect(jsonPath("$.fuelType").value("Gasolina"))
                .andExpect(jsonPath("$.hasAC").value(true))
                .andExpect(jsonPath("$.hasGPS").value(false))
                .andExpect(jsonPath("$.hasBluetooth").value(true))
                .andExpect(jsonPath("$.description").value("Carro económico perfeito para cidade"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/fiat500.jpg"));
    }
}
