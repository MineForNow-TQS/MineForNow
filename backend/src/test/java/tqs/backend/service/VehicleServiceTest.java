package tqs.backend.service;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.backend.dto.CreateVehicleRequest;
import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.mapper.VehicleMapper;
import tqs.backend.model.User;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;
import tqs.backend.model.UserRole;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

/**
 * Testes unitários do VehicleService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Unit Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    // ALTERAÇÃO: era @Mock; deve ser @Spy para não devolver null e estragar Optional.map(...)
    @Spy
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle testVehicle;
    private User testOwner;
    private User testClient;
    private CreateVehicleRequest createRequest;

    @BeforeEach
    void setUp() {
        testOwner = User.builder()
                .id(1L)
                .email("owner@minefornow.com")
                .fullName("João Silva")
                .role(UserRole.OWNER)
                .build();

        testClient = User.builder()
                .id(2L)
                .email("client@minefornow.com")
                .fullName("Maria Santos")
                .role(UserRole.RENTER)
                .build();

        testVehicle = Vehicle.builder()
                .id(1L)
                .owner(testOwner)
                .brand("Fiat")
                .model("500")
                .year(2020)
                .type("Citadino")
                .pricePerDay(BigDecimal.valueOf(25.0))
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

        createRequest = new CreateVehicleRequest();
        createRequest.setBrand("Toyota");
        createRequest.setModel("Corolla");
        createRequest.setYear(2022);
        createRequest.setType("Sedan");
        createRequest.setPricePerDay(35.0);
        createRequest.setCity("Porto");
        createRequest.setFuelType("Híbrido");
        createRequest.setTransmission("Automático");
        createRequest.setSeats(5);
        createRequest.setDoors(4);
        createRequest.setHasAC(true);
        createRequest.setHasGPS(true);
        createRequest.setHasBluetooth(true);
        createRequest.setLicensePlate("AA-12-BB");
        createRequest.setMileage(15000);
        createRequest.setDescription("Veículo confortável e económico");
    }

    // ==================== TESTES PARA getVehicleById (SCRUM-12)
    // ====================

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
    @DisplayName("Quando buscar veículo com ID null, deve retornar vazio")
    void whenGetVehicleByNullId_thenReturnEmpty() {
        // Act
        Optional<VehicleDetailDTO> result = vehicleService.getVehicleById(null);

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

    // ==================== TESTES PARA createVehicle (SCRUM-10)
    // ====================

    @Test
    @Requirement("SCRUM-10")
    @DisplayName("Quando owner válido cria veículo, deve retornar veículo criado")
    void whenOwnerCreatesVehicle_thenReturnCreatedVehicle() {
        // Arrange
        when(userRepository.findByEmail("owner@minefornow.com")).thenReturn(Optional.of(testOwner));

        Vehicle savedVehicle = Vehicle.builder()
                .id(2L)
                .owner(testOwner)
                .brand(createRequest.getBrand())
                .model(createRequest.getModel())
                .year(createRequest.getYear())
                .pricePerDay(BigDecimal.valueOf(createRequest.getPricePerDay()))
                .city(createRequest.getCity())
                .build();

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        // Act
        Vehicle result = vehicleService.createVehicle(createRequest, "owner@minefornow.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getBrand()).isEqualTo("Toyota");
        assertThat(result.getModel()).isEqualTo("Corolla");
        assertThat(result.getOwner()).isEqualTo(testOwner);

        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @Requirement("SCRUM-10")
    @DisplayName("Quando user não existe, deve lançar IllegalArgumentException")
    void whenUserNotFound_thenThrowException() {
        // Arrange
        when(userRepository.findByEmail("unknown@minefornow.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.createVehicle(createRequest, "unknown@minefornow.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Utilizador não encontrado");

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @Requirement("SCRUM-10")
    @DisplayName("Quando user não é OWNER, deve lançar IllegalArgumentException")
    void whenUserIsNotOwner_thenThrowException() {
        // Arrange
        when(userRepository.findByEmail("client@minefornow.com")).thenReturn(Optional.of(testClient));

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.createVehicle(createRequest, "client@minefornow.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Apenas proprietários podem registar veículos");

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @Requirement("SCRUM-10")
    @DisplayName("Quando criar veículo, deve mapear todos os campos corretamente")
    void whenCreateVehicle_thenAllFieldsAreMapped() {
        // Arrange
        when(userRepository.findByEmail("owner@minefornow.com")).thenReturn(Optional.of(testOwner));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle v = invocation.getArgument(0);
            v.setId(10L);
            return v;
        });

        // Act
        Vehicle result = vehicleService.createVehicle(createRequest, "owner@minefornow.com");

        // Assert
        assertThat(result.getBrand()).isEqualTo("Toyota");
        assertThat(result.getModel()).isEqualTo("Corolla");
        assertThat(result.getYear()).isEqualTo(2022);
        assertThat(result.getType()).isEqualTo("Sedan");
        assertThat(result.getPricePerDay()).isEqualByComparingTo(BigDecimal.valueOf(35.0));
        assertThat(result.getCity()).isEqualTo("Porto");
        assertThat(result.getFuelType()).isEqualTo("Híbrido");
        assertThat(result.getTransmission()).isEqualTo("Automático");
        assertThat(result.getSeats()).isEqualTo(5);
        assertThat(result.getDoors()).isEqualTo(4);
        assertThat(result.getHasAC()).isTrue();
        assertThat(result.getHasGPS()).isTrue();
        assertThat(result.getHasBluetooth()).isTrue();
        assertThat(result.getLicensePlate()).isEqualTo("AA-12-BB");
        assertThat(result.getMileage()).isEqualTo(15000);
    }

    // ==================== TESTES PARA updateVehicle (SCRUM-7)
    // ====================

    @Test
    @Requirement("SCRUM-10")
    @DisplayName("Quando atualizar veículo inexistente, deve lançar exceção")
    void whenUpdateNonExistentVehicle_thenThrowException() {
        // Arrange
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.updateVehicle(999L, createRequest, "owner@minefornow.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Veículo não encontrado");

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @Requirement("SCRUM-10")
    @DisplayName("Quando não owner tenta atualizar, deve lançar exceção")
    void whenNotOwnerUpdatesVehicle_thenThrowException() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.updateVehicle(1L, createRequest, "client@minefornow.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Apenas o proprietário pode editar este veículo");

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @Requirement("SCRUM-10")
    @DisplayName("Quando atualizar veículo com sucesso, deve atualizar campos e salvar")
    void whenUpdateVehicleSuccess_thenUpdateFieldsAndSave() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Vehicle updatedVehicle = vehicleService.updateVehicle(1L, createRequest, "owner@minefornow.com");

        // Assert
        assertThat(updatedVehicle.getBrand()).isEqualTo("Toyota");
        assertThat(updatedVehicle.getModel()).isEqualTo("Corolla");
        assertThat(updatedVehicle.getYear()).isEqualTo(2022);
        assertThat(updatedVehicle.getPricePerDay()).isEqualByComparingTo(BigDecimal.valueOf(35.0));
        assertThat(updatedVehicle.getFuelType()).isEqualTo("Híbrido");
        assertThat(updatedVehicle.getCity()).isEqualTo("Porto");
        assertThat(updatedVehicle.getType()).isEqualTo("Sedan");
        assertThat(updatedVehicle.getLicensePlate()).isEqualTo("AA-12-BB");
        assertThat(updatedVehicle.getMileage()).isEqualTo(15000);
        assertThat(updatedVehicle.getTransmission()).isEqualTo("Automático");
        assertThat(updatedVehicle.getSeats()).isEqualTo(5);
        assertThat(updatedVehicle.getDoors()).isEqualTo(4);
        assertThat(updatedVehicle.getHasAC()).isTrue();
        assertThat(updatedVehicle.getHasGPS()).isTrue();
        assertThat(updatedVehicle.getHasBluetooth()).isTrue();
        assertThat(updatedVehicle.getDescription()).isEqualTo("Veículo confortável e económico");

        verify(vehicleRepository).save(testVehicle);
    }

    @Test
    @Requirement("SCRUM-10")
    @DisplayName("Quando eliminar veículo inexistente, deve lançar exceção")
    void whenDeleteNonExistentVehicle_thenThrowException() {
        // Arrange
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.deleteVehicle(999L, "owner@minefornow.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Veículo não encontrado");

        verify(vehicleRepository, never()).delete(any(Vehicle.class));
    }

    @Test
    @Requirement("SCRUM-10")
    @DisplayName("Quando não owner tenta eliminar, deve lançar exceção")
    void whenNotOwnerDeletesVehicle_thenThrowException() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.deleteVehicle(1L, "client@minefornow.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Apenas o proprietário pode eliminar este veículo");

        verify(vehicleRepository, never()).delete(any(Vehicle.class));
    }

    @Test
    @Requirement("SCRUM-7")
    @DisplayName("Quando eliminar veículo com sucesso, deve chamar delete do repositório")
    void whenDeleteVehicleSuccess_thenCallDelete() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act
        vehicleService.deleteVehicle(1L, "owner@minefornow.com");

        // Assert
        verify(vehicleRepository).delete(testVehicle);
    }
}
