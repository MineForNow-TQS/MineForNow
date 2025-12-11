package tqs.backend.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
// lombok removed: generating constructors, getters and setters manually

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Owner ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

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

    // No-arg constructor
    public Vehicle() {
    }

    // All-args constructor
    public Vehicle(Long id, User owner, String brand, String model, Integer year, String type, String licensePlate,
                   Integer mileage, String fuelType, String transmission, Integer seats, Integer doors,
                   Boolean hasAC, Boolean hasGPS, Boolean hasBluetooth, String city, String exactLocation,
                   Double pricePerDay, String description, String imageUrl) {
        this.id = id;
        this.owner = owner;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.type = type;
        this.licensePlate = licensePlate;
        this.mileage = mileage;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.seats = seats;
        this.doors = doors;
        this.hasAC = hasAC;
        this.hasGPS = hasGPS;
        this.hasBluetooth = hasBluetooth;
        this.city = city;
        this.exactLocation = exactLocation;
        this.pricePerDay = pricePerDay;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }

    public Integer getDoors() { return doors; }
    public void setDoors(Integer doors) { this.doors = doors; }

    public Boolean getHasAC() { return hasAC; }
    public void setHasAC(Boolean hasAC) { this.hasAC = hasAC; }

    public Boolean getHasGPS() { return hasGPS; }
    public void setHasGPS(Boolean hasGPS) { this.hasGPS = hasGPS; }

    public Boolean getHasBluetooth() { return hasBluetooth; }
    public void setHasBluetooth(Boolean hasBluetooth) { this.hasBluetooth = hasBluetooth; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getExactLocation() { return exactLocation; }
    public void setExactLocation(String exactLocation) { this.exactLocation = exactLocation; }

    public Double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(Double pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Builder (manual)
    public static VehicleBuilder builder() { return new VehicleBuilder(); }

    public static class VehicleBuilder {
        private Long id;
        private User owner;
        private String brand;
        private String model;
        private Integer year;
        private String type;
        private String licensePlate;
        private Integer mileage;
        private String fuelType;
        private String transmission;
        private Integer seats;
        private Integer doors;
        private Boolean hasAC;
        private Boolean hasGPS;
        private Boolean hasBluetooth;
        private String city;
        private String exactLocation;
        private Double pricePerDay;
        private String description;
        private String imageUrl;

        public VehicleBuilder id(Long id) { this.id = id; return this; }
        public VehicleBuilder owner(User owner) { this.owner = owner; return this; }
        public VehicleBuilder brand(String brand) { this.brand = brand; return this; }
        public VehicleBuilder model(String model) { this.model = model; return this; }
        public VehicleBuilder year(Integer year) { this.year = year; return this; }
        public VehicleBuilder type(String type) { this.type = type; return this; }
        public VehicleBuilder licensePlate(String licensePlate) { this.licensePlate = licensePlate; return this; }
        public VehicleBuilder mileage(Integer mileage) { this.mileage = mileage; return this; }
        public VehicleBuilder fuelType(String fuelType) { this.fuelType = fuelType; return this; }
        public VehicleBuilder transmission(String transmission) { this.transmission = transmission; return this; }
        public VehicleBuilder seats(Integer seats) { this.seats = seats; return this; }
        public VehicleBuilder doors(Integer doors) { this.doors = doors; return this; }
        public VehicleBuilder hasAC(Boolean hasAC) { this.hasAC = hasAC; return this; }
        public VehicleBuilder hasGPS(Boolean hasGPS) { this.hasGPS = hasGPS; return this; }
        public VehicleBuilder hasBluetooth(Boolean hasBluetooth) { this.hasBluetooth = hasBluetooth; return this; }
        public VehicleBuilder city(String city) { this.city = city; return this; }
        public VehicleBuilder exactLocation(String exactLocation) { this.exactLocation = exactLocation; return this; }
        public VehicleBuilder pricePerDay(Double pricePerDay) { this.pricePerDay = pricePerDay; return this; }
        public VehicleBuilder description(String description) { this.description = description; return this; }
        public VehicleBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }

        public Vehicle build() {
            return new Vehicle(id, owner, brand, model, year, type, licensePlate, mileage, fuelType, transmission,
                    seats, doors, hasAC, hasGPS, hasBluetooth, city, exactLocation, pricePerDay, description, imageUrl);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}