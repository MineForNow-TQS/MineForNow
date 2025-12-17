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

    /**
     * Atualiza um veículo existente.
     * Apenas o owner do veículo pode atualizá-lo.
     * 
     * @param id         ID do veículo a atualizar
     * @param request    DTO com os novos dados
     * @param ownerEmail email do owner autenticado
     * @return o veículo atualizado
     * @throws IllegalArgumentException se o veículo não existir ou o user não for o
     *                                  owner
     */
    public Vehicle updateVehicle(Long id, CreateVehicleRequest request, String ownerEmail) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));

        // Verificar que o user autenticado é o owner do veículo
        if (!vehicle.getOwner().getEmail().equals(ownerEmail)) {
            throw new IllegalArgumentException("Apenas o proprietário pode editar este veículo");
        }

        // Atualizar os campos
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setPricePerDay(request.getPricePerDay());
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

    /**
     * Elimina um veículo existente.
     * Apenas o owner do veículo pode eliminá-lo.
     * 
     * @param id         ID do veículo a eliminar
     * @param ownerEmail email do owner autenticado
     * @throws IllegalArgumentException se o veículo não existir ou o user não for o
     *                                  owner
     */
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
