package tqs.backend.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}