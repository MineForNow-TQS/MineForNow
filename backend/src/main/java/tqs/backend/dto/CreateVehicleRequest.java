package tqs.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para criação de um novo veículo.
 * Contém os campos obrigatórios e opcionais para registar um veículo na
 * plataforma.
 */
public class CreateVehicleRequest {

    // --- Campos Obrigatórios ---
    @NotBlank(message = "A marca é obrigatória")
    private String brand;

    @NotBlank(message = "O modelo é obrigatório")
    private String model;

    @NotNull(message = "O ano é obrigatório")
    private Integer year;

    @NotNull(message = "O preço por dia é obrigatório")
    @Positive(message = "O preço por dia deve ser positivo")
    private Double pricePerDay;

    @NotBlank(message = "O tipo de combustível é obrigatório")
    private String fuelType;

    @NotBlank(message = "A cidade é obrigatória")
    private String city;

    // --- Campos Opcionais ---
    private String type; // ex: Citadino, SUV, Moto
    private String licensePlate;
    private Integer mileage;
    private String transmission;
    private Integer seats;
    private Integer doors;
    private Boolean hasAC;
    private Boolean hasGPS;
    private Boolean hasBluetooth;
    private String exactLocation;
    private String description;
    private String imageUrl;

    // --- Constructors ---
    public CreateVehicleRequest() {
    }

    // --- Getters and Setters ---
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public Integer getDoors() {
        return doors;
    }

    public void setDoors(Integer doors) {
        this.doors = doors;
    }

    public Boolean getHasAC() {
        return hasAC;
    }

    public void setHasAC(Boolean hasAC) {
        this.hasAC = hasAC;
    }

    public Boolean getHasGPS() {
        return hasGPS;
    }

    public void setHasGPS(Boolean hasGPS) {
        this.hasGPS = hasGPS;
    }

    public Boolean getHasBluetooth() {
        return hasBluetooth;
    }

    public void setHasBluetooth(Boolean hasBluetooth) {
        this.hasBluetooth = hasBluetooth;
    }

    public String getExactLocation() {
        return exactLocation;
    }

    public void setExactLocation(String exactLocation) {
        this.exactLocation = exactLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
