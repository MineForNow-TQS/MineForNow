package tqs.backend.service;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.model.User;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.VehicleRepository;
import tqs.backend.model.UserRole;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do VehicleService (SCRUM-12).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Unit Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle testVehicle;
    private User testOwner;

    @BeforeEach
    void setUp() {
        testOwner = User.builder()
                .id(1L)
                .email("owner@minefornow.com")
                .fullName("João Silva")
                .role(UserRole.OWNER)
                .build();

        testVehicle = Vehicle.builder()
                .id(1L)
                .owner(testOwner)
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
                .build();
    }

    @Test
    @Requirement("SCRUM-12")
    @DisplayName("Quando buscar veículo por ID existente, deve retornar DTO com dados corretos")
    void whenGetVehicleById_thenReturnDTO() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act
        Optional<VehicleDetailDTO> result = vehicleService.getVehicleById(1L);

        // Assert
        assertThat(result).isPresent();
        VehicleDetailDTO dto = result.get();
        
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getBrand()).isEqualTo("Fiat");
        assertThat(dto.getModel()).isEqualTo("500");
        assertThat(dto.getYear()).isEqualTo(2020);
        assertThat(dto.getPricePerDay()).isEqualTo(25.0);
        assertThat(dto.getCity()).isEqualTo("Lisboa");
        assertThat(dto.getDisplayName()).isEqualTo("Fiat 500 2020");
        assertThat(dto.getFormattedPrice()).isEqualTo("25.00 €/dia");
    }

    @Test
    @Requirement("SCRUM-12")
    @DisplayName("Quando buscar veículo por ID inexistente, deve retornar vazio")
    void whenGetVehicleByInvalidId_thenReturnEmpty() {
        // Arrange
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<VehicleDetailDTO> result = vehicleService.getVehicleById(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @Requirement("SCRUM-12")
    @DisplayName("Quando veículo não tem ano, displayName deve conter apenas marca e modelo")
    void whenVehicleHasNoYear_thenDisplayNameContainsOnlyBrandAndModel() {
        // Arrange
        testVehicle.setYear(null);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act
        Optional<VehicleDetailDTO> result = vehicleService.getVehicleById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getDisplayName()).isEqualTo("Fiat 500");
    }

    @Test
    @Requirement("SCRUM-12")
    @DisplayName("Quando preço é null, formattedPrice deve retornar N/A")
    void whenPriceIsNull_thenFormattedPriceReturnsNA() {
        // Arrange
        testVehicle.setPricePerDay(null);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act
        Optional<VehicleDetailDTO> result = vehicleService.getVehicleById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getFormattedPrice()).isEqualTo("N/A");
    }

    @Test
    @Requirement("SCRUM-12")
    @DisplayName("Quando veículo tem owner, deve retornar ownerName e ownerEmail no DTO")
    void whenVehicleHasOwner_thenDTOContainsOwnerInfo() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act
        Optional<VehicleDetailDTO> result = vehicleService.getVehicleById(1L);

        // Assert
        assertThat(result).isPresent();
        VehicleDetailDTO dto = result.get();
        assertThat(dto.getOwnerName()).isEqualTo("João Silva");
        assertThat(dto.getOwnerEmail()).isEqualTo("owner@minefornow.com");
    }
}
