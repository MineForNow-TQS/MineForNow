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

import java.util.Optional;

/**
 * Service para operações de negócio relacionadas a veículos.
 */
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
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

    /**
     * Cria um novo veículo associado ao owner identificado pelo email.
     * 
     * @param request    DTO com os dados do veículo
     * @param ownerEmail email do owner autenticado
     * @return o veículo criado
     * @throws IllegalArgumentException se o user não existir ou não for OWNER
     */
    public Vehicle createVehicle(CreateVehicleRequest request, String ownerEmail) {
        // Buscar user pelo email
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado: " + ownerEmail));

        // Validar que o user é OWNER
        if (owner.getRole() != UserRole.OWNER) {
            throw new IllegalArgumentException("Apenas proprietários podem registar veículos");
        }

        // Criar o veículo usando o builder
        Vehicle vehicle = Vehicle.builder()
                .owner(owner)
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .pricePerDay(request.getPricePerDay())
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
}
