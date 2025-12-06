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
            @RequestParam String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickup,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dropoff) {

        if (pickup != null && dropoff != null) {
            // Se tiver datas, filtra por disponibilidade
            return vehicleRepository.findAvailableVehicles(city, pickup, dropoff);
        } else {
            // Se só tiver cidade, filtra só por cidade
            return vehicleRepository.findByCityContainingIgnoreCase(city);
        }
    }
}