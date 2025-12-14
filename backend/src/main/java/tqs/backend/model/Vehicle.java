package tqs.backend.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicles_id_seq")
    @SequenceGenerator(name = "vehicles_id_seq", sequenceName = "vehicles_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "car_type", nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private Integer mileage;

    @Column(name = "license_plate", nullable = false, unique = true, length = 50)
    private String licensePlate;

    @Column(name = "fuel_type", nullable = false, length = 50)
    private String fuelType;

    @Column(nullable = false, length = 50)
    private String transmission;

    @Column(nullable = false)
    private Integer seats;

    @Column(nullable = false)
    private Integer doors;

    @Column(name = "air_conditioning", nullable = false)
    private Boolean hasAC;

    @Column(name = "gps", nullable = false)
    private Boolean hasGPS;

    @Column(name = "bluetooth", nullable = false)
    private Boolean hasBluetooth;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "location", nullable = false, length = 255)
    private String exactLocation;

    @Column(name = "daily_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerDay;

    @Column(nullable = false, length = 10)
    private String currency; 

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = false, length = 20)
    private String status; 

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Transient
    private String imageUrl;

    @PrePersist
    void prePersist() {
        if (hasAC == null) hasAC = false;
        if (hasGPS == null) hasGPS = false;
        if (hasBluetooth == null) hasBluetooth = false;
        if (currency == null || currency.isBlank()) currency = "EUR";
        if (status == null || status.isBlank()) status = "VISIBLE";

        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
