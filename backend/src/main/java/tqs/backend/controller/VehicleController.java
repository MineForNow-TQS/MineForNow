package tqs.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// lombok removed: add explicit constructor for dependency injection
import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.VehicleRepository;
import tqs.backend.service.VehicleService;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "http://localhost:3000")
public class VehicleController {

    private final VehicleRepository vehicleRepository;
    private final VehicleService vehicleService;

    // Explicit constructor replacing Lombok @RequiredArgsConstructor
    public VehicleController(VehicleRepository vehicleRepository, VehicleService vehicleService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Endpoint para buscar detalhes de um veículo específico (SCRUM-12).
     * 
     * @param id ID do veículo
     * @return 200 OK com os detalhes do veículo, ou 404 Not Found se não existir
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDetailDTO> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // NOVO ENDPOINT: /api/vehicles/search?city=Lisboa&pickup=2025-12-10&dropoff=2025-12-12
    @GetMapping("/search")
    public List<Vehicle> searchVehicles(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickup,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dropoff) {

        // Se não houver nenhum filtro, retorna todos os veículos
        if (city == null && pickup == null && dropoff == null) {
            return vehicleRepository.findAll();
        }

        // Se tiver datas e cidade, filtra por disponibilidade
        if (city != null && pickup != null && dropoff != null) {
            return vehicleRepository.findAvailableVehicles(city, pickup, dropoff);
        }

        // Se só tiver datas (sem cidade), filtra por disponibilidade em todas as cidades
        if (pickup != null && dropoff != null) {
            return vehicleRepository.findAvailableVehiclesByDates(pickup, dropoff);
        }

        // Se só tiver cidade, filtra por cidade
        if (city != null) {
            return vehicleRepository.findByCityContainingIgnoreCase(city);
        }

        // Se não tiver filtros, retorna todos
        return vehicleRepository.findAll();
    }
}