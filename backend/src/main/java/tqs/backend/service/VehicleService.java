package tqs.backend.service;

import org.springframework.stereotype.Service;
import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.mapper.VehicleMapper;
import tqs.backend.repository.VehicleRepository;

import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    public Optional<VehicleDetailDTO> getVehicleById(Long id) {
        if (id == null) return Optional.empty();
        return vehicleRepository.findById(id).map(vehicleMapper::toDetailDTO);
    }
}
