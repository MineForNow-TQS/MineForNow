package tqs.backend.model;

public class Car {
    private Long id;
    private String brand;
    private String model;
    private String location;
    private double pricePerDay;
    private String imageUrl;

    public Car(Long id, String brand, String model, String location, double pricePerDay, String imageUrl) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.location = location;
        this.pricePerDay = pricePerDay;
        this.imageUrl = imageUrl;
    }

    // Getters obrigatórios para o JSON
    public Long getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getLocation() { return location; }
    public double getPricePerDay() { return pricePerDay; }
    public String getImageUrl() { return imageUrl; }
}