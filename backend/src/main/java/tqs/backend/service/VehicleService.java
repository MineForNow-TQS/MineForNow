package tqs.backend.service;

import org.springframework.stereotype.Service;
import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.mapper.VehicleMapper;
import tqs.backend.repository.VehicleRepository;

import java.util.Optional;

/**
 * Service para operações de negócio relacionadas a veículos.
 */
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Busca um veículo por ID e retorna o DTO detalhado.
     * 
     * @param id ID do veículo
     * @return Optional com o DTO se encontrado, vazio caso contrário
     */
    public Optional<VehicleDetailDTO> getVehicleById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return vehicleRepository.findById(id).map(VehicleMapper::toDetailDTO);
    }
}
