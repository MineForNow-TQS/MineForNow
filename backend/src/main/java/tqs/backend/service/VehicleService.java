package tqs.backend.service;

import org.springframework.stereotype.Service;
import tqs.backend.dto.CreateVehicleRequest;
import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.mapper.VehicleMapper;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service para operações de negócio relacionadas a veículos.
 */
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository, UserRepository userRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.vehicleMapper = vehicleMapper;
    }

    public Optional<VehicleDetailDTO> getVehicleById(Long id) {
        if (id == null) return Optional.empty();
        return vehicleRepository.findById(id).map(vehicleMapper::toDetailDTO);
    }

    public Vehicle createVehicle(CreateVehicleRequest request, String ownerEmail) {
        // Buscar user pelo email
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado: " + ownerEmail));

        // Validar que o user é OWNER
        if (owner.getRole() != UserRole.OWNER) {
            throw new IllegalArgumentException("Apenas proprietários podem registar veículos");
        }

        // Converter pricePerDay (request: Double -> entity: BigDecimal)
        BigDecimal pricePerDay = (request.getPricePerDay() == null)
                ? null
                : BigDecimal.valueOf(request.getPricePerDay());

        // Criar o veículo usando o builder
        Vehicle vehicle = Vehicle.builder()
                .owner(owner)
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .pricePerDay(pricePerDay)
                .fuelType(request.getFuelType())
                .city(request.getCity())
                .type(request.getType())
                .licensePlate(request.getLicensePlate())
                .mileage(request.getMileage())
                .transmission(request.getTransmission())
                .seats(request.getSeats())
                .doors(request.getDoors())
                .hasAC(request.getHasAC())
                .hasGPS(request.getHasGPS())
                .hasBluetooth(request.getHasBluetooth())
                .exactLocation(request.getExactLocation())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();

        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, CreateVehicleRequest request, String ownerEmail) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));

        // Verificar que o user autenticado é o owner do veículo
        if (!vehicle.getOwner().getEmail().equals(ownerEmail)) {
            throw new IllegalArgumentException("Apenas o proprietário pode editar este veículo");
        }

        // Converter pricePerDay (request: Double -> entity: BigDecimal)
        BigDecimal pricePerDay = (request.getPricePerDay() == null)
                ? null
                : BigDecimal.valueOf(request.getPricePerDay());

        // Atualizar os campos
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setPricePerDay(pricePerDay);
        vehicle.setFuelType(request.getFuelType());
        vehicle.setCity(request.getCity());
        vehicle.setType(request.getType());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setMileage(request.getMileage());
        vehicle.setTransmission(request.getTransmission());
        vehicle.setSeats(request.getSeats());
        vehicle.setDoors(request.getDoors());
        vehicle.setHasAC(request.getHasAC());
        vehicle.setHasGPS(request.getHasGPS());
        vehicle.setHasBluetooth(request.getHasBluetooth());
        vehicle.setExactLocation(request.getExactLocation());
        vehicle.setDescription(request.getDescription());
        vehicle.setImageUrl(request.getImageUrl());

        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id, String ownerEmail) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));

        // Verificar que o user autenticado é o owner do veículo
        if (!vehicle.getOwner().getEmail().equals(ownerEmail)) {
            throw new IllegalArgumentException("Apenas o proprietário pode eliminar este veículo");
        }

        vehicleRepository.delete(vehicle);
    }
}
