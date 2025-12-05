package tqs.backend.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relações JPA
    @ManyToOne
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User ownerUser;

    @OneToMany(mappedBy = "Car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "Car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarBlock> blocks;

    @OneToMany(mappedBy = "Car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Incident> incidents;

    @OneToMany(mappedBy = "Car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarImage> images;

    private String brand;
    private String model;
    private int year;
    private String type;
    private double kilometers;
    private FuelType fuelType;
    private TransmissionType transmission;
    private int seats;
    private int doors;
    private boolean ac;
    private boolean gps;
    private boolean bluetooth;
    private String city;
    private String exactLocation;
    private double pricePerDay;
    private String description;

    // Construtor padrão
    public Car() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getOwnerUser() { return ownerUser; }
    public void setOwnerUser(User ownerUser) { this.ownerUser = ownerUser; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getKilometers() { return kilometers; }
    public void setKilometers(double kilometers) { this.kilometers = kilometers; }
    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }
    public TransmissionType getTransmission() { return transmission; }
    public void setTransmission(TransmissionType transmission) { this.transmission = transmission; }
    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }
    public int getDoors() { return doors; }
    public void setDoors(int doors) { this.doors = doors; }
    public boolean isAc() { return ac; }
    public void setAc(boolean ac) { this.ac = ac; }
    public boolean isGps() { return gps; }
    public void setGps(boolean gps) { this.gps = gps; }
    public boolean isBluetooth() { return bluetooth; }
    public void setBluetooth(boolean bluetooth) { this.bluetooth = bluetooth; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getExactLocation() { return exactLocation; }
    public void setExactLocation(String exactLocation) { this.exactLocation = exactLocation; }
    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    public List<CarBlock> getBlocks() { return blocks; }
    public void setBlocks(List<CarBlock> blocks) { this.blocks = blocks; }
    public List<Incident> getIncidents() { return incidents; }
    public void setIncidents(List<Incident> incidents) { this.incidents = incidents; }
    public List<CarImage> getImages() { return images; }
    public void setImages(List<CarImage> images) { this.images = images; }
}