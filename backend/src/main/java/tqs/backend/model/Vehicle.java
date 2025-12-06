package tqs.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Informações Básicas ---
    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(name = "manufacturing_year")
    private Integer year;

    private String type; // ex: Citadino, SUV, Moto

    @Column(unique = true)
    private String licensePlate;

    private Integer mileage;

    // --- Especificações ---
    private String fuelType;
    private String transmission;
    private Integer seats;
    private Integer doors;

    // --- Características ---
    private Boolean hasAC;
    private Boolean hasGPS;
    private Boolean hasBluetooth;

    // --- Localização e Preço ---
    private String city;
    private String exactLocation;
    
    @Column(nullable = false)
    private Double pricePerDay;

    // --- Outros ---
    @Column(length = 1000)
    private String description;

    private String imageUrl;
}