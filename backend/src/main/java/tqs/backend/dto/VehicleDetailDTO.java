package tqs.backend.dto;

// lombok removed: generate constructors/getters/setters manually

/**
 * DTO detalhado para exibição completa de informações do veículo.
 * Usado na página de detalhes do veículo (SCRUM-12).
 */
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

    // No-arg constructor
    public VehicleDetailDTO() {
    }

    // All-args constructor
    public VehicleDetailDTO(Long id, String brand, String model, Integer year, String type, String licensePlate,
                            Integer mileage, String fuelType, String transmission, Integer seats, Integer doors,
                            Boolean hasAC, Boolean hasGPS, Boolean hasBluetooth, String city, String exactLocation,
                            Double pricePerDay, String description, String imageUrl, String displayName,
                            String formattedPrice, String ownerName, String ownerEmail) {
        this.id = id;
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
        this.displayName = displayName;
        this.formattedPrice = formattedPrice;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
    }

    // Getters and setters (selected)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getFormattedPrice() { return formattedPrice; }
    public void setFormattedPrice(String formattedPrice) { this.formattedPrice = formattedPrice; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    // Builder (manual)
    public static VehicleDetailDTOBuilder builder() { return new VehicleDetailDTOBuilder(); }

    public static class VehicleDetailDTOBuilder {
        private Long id;
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
        private String displayName;
        private String formattedPrice;
        private String ownerName;
        private String ownerEmail;

        public VehicleDetailDTOBuilder id(Long id) { this.id = id; return this; }
        public VehicleDetailDTOBuilder brand(String brand) { this.brand = brand; return this; }
        public VehicleDetailDTOBuilder model(String model) { this.model = model; return this; }
        public VehicleDetailDTOBuilder year(Integer year) { this.year = year; return this; }
        public VehicleDetailDTOBuilder type(String type) { this.type = type; return this; }
        public VehicleDetailDTOBuilder licensePlate(String licensePlate) { this.licensePlate = licensePlate; return this; }
        public VehicleDetailDTOBuilder mileage(Integer mileage) { this.mileage = mileage; return this; }
        public VehicleDetailDTOBuilder fuelType(String fuelType) { this.fuelType = fuelType; return this; }
        public VehicleDetailDTOBuilder transmission(String transmission) { this.transmission = transmission; return this; }
        public VehicleDetailDTOBuilder seats(Integer seats) { this.seats = seats; return this; }
        public VehicleDetailDTOBuilder doors(Integer doors) { this.doors = doors; return this; }
        public VehicleDetailDTOBuilder hasAC(Boolean hasAC) { this.hasAC = hasAC; return this; }
        public VehicleDetailDTOBuilder hasGPS(Boolean hasGPS) { this.hasGPS = hasGPS; return this; }
        public VehicleDetailDTOBuilder hasBluetooth(Boolean hasBluetooth) { this.hasBluetooth = hasBluetooth; return this; }
        public VehicleDetailDTOBuilder city(String city) { this.city = city; return this; }
        public VehicleDetailDTOBuilder exactLocation(String exactLocation) { this.exactLocation = exactLocation; return this; }
        public VehicleDetailDTOBuilder pricePerDay(Double pricePerDay) { this.pricePerDay = pricePerDay; return this; }
        public VehicleDetailDTOBuilder description(String description) { this.description = description; return this; }
        public VehicleDetailDTOBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public VehicleDetailDTOBuilder displayName(String displayName) { this.displayName = displayName; return this; }
        public VehicleDetailDTOBuilder formattedPrice(String formattedPrice) { this.formattedPrice = formattedPrice; return this; }
        public VehicleDetailDTOBuilder ownerName(String ownerName) { this.ownerName = ownerName; return this; }
        public VehicleDetailDTOBuilder ownerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; return this; }

        public VehicleDetailDTO build() {
            return new VehicleDetailDTO(id, brand, model, year, type, licensePlate, mileage, fuelType, transmission,
                    seats, doors, hasAC, hasGPS, hasBluetooth, city, exactLocation, pricePerDay, description, imageUrl,
                    displayName, formattedPrice, ownerName, ownerEmail);
        }
    }
}
