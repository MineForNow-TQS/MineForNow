package tqs.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO detalhado para exibição completa de informações do veículo.
 * Usado na página de detalhes do veículo (SCRUM-12).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDetailDTO {

    private Long id;

    // --- Informações Básicas ---
    private String brand;
    private String model;
    private Integer year;
    private String type;
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
    private Double pricePerDay;

    // --- Outros ---
    private String description;
    private String imageUrl;

    // --- Campos Calculados/Formatados ---
    private String displayName; // ex: "Fiat 500 2020"
    private String formattedPrice; // ex: "25.00 €/dia"

    // --- Informações do Proprietário ---
    private String ownerName; // Nome do proprietário do veículo
    private String ownerEmail; // Email do proprietário
}
