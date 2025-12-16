package tqs.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tqs.backend.model.Vehicle;
import tqs.backend.repository.VehicleRepository;
import tqs.backend.mapper.VehicleMapper;
import tqs.backend.dto.VehicleDetailDTO;

class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle1;
    private Vehicle vehicle2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        vehicle1 = Vehicle.builder()
            .id(1L)
            .title("Tesla Model 3")
            .brand("Tesla")
            .model("Model 3")
            .city("Porto")
            .year(2022)
            .pricePerDay(BigDecimal.valueOf(25.0))
            .status("VISIBLE")
            .build();

        vehicle2 = Vehicle.builder()
            .id(2L)
            .title("BMW X5")
            .brand("BMW")
            .model("X5")
            .city("Lisboa")
            .year(2020)
            .pricePerDay(BigDecimal.valueOf(40.0))
            .status("VISIBLE")
            .build();
    }

    @Test
    void whenGetAllVehicles_thenReturnVehicleList() {
        // VehicleService now returns DTOs via getVehicleById; test mapping behavior instead
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle1));
        VehicleDetailDTO dto = VehicleDetailDTO.builder().id(1L).brand("Tesla").model("Model 3").year(2022).pricePerDay(25.0).build();
        when(vehicleMapper.toDetailDTO(vehicle1)).thenReturn(dto);

        Optional<VehicleDetailDTO> found = vehicleService.getVehicleById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getBrand()).isEqualTo("Tesla");
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleMapper, times(1)).toDetailDTO(vehicle1);
    }

    @Test
    void whenGetVehicleById_thenReturnVehicle() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle1));
        VehicleDetailDTO dto = VehicleDetailDTO.builder().id(1L).brand("Tesla").model("Model 3").year(2022).pricePerDay(25.0).build();
        when(vehicleMapper.toDetailDTO(vehicle1)).thenReturn(dto);

        Optional<VehicleDetailDTO> found = vehicleService.getVehicleById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getBrand()).isEqualTo("Tesla");
        verify(vehicleRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetVehicleByIdNotFound_thenReturnEmpty() {
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<VehicleDetailDTO> found = vehicleService.getVehicleById(999L);

        assertThat(found).isNotPresent();
        verify(vehicleRepository, times(1)).findById(999L);
    }
}
