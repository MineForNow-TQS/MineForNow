package tqs.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.VehicleRepository;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
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

        // Se só tiver cidade, filtra por cidade
        if (city != null) {
            return vehicleRepository.findByCityContainingIgnoreCase(city);
        }

        // Se só tiver datas (sem cidade), retorna todos os veículos
        // (pode ser melhorado para filtrar por datas em todas as cidades)
        return vehicleRepository.findAll();
    }
}