package tqs.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
// lombok removed: add explicit constructor for dependency injection
import tqs.backend.dto.CreateVehicleRequest;
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
     * Endpoint para criar um novo veículo (SCRUM-7).
     * Requer autenticação - apenas Owners podem criar veículos.
     * 
     * @param request     dados do veículo a criar
     * @param userDetails informação do utilizador autenticado
     * @return 201 Created com o veículo criado, ou 400 Bad Request se validação
     *         falhar
     */
    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(
            @Valid @RequestBody CreateVehicleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Vehicle vehicle = vehicleService.createVehicle(request, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(vehicle);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
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

    // NOVO ENDPOINT:
    // /api/vehicles/search?city=Lisboa&pickup=2025-12-10&dropoff=2025-12-12
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

        // Se só tiver datas (sem cidade), filtra por disponibilidade em todas as
        // cidades
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