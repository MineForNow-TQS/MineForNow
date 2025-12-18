package tqs.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class BookingDTO {
    private Long id;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private Long vehicleId;
    private Long renterId;
    private String status;
    private BigDecimal totalPrice;
    private String currency;
    private OffsetDateTime createdAt;

    public BookingDTO() {}

    public BookingDTO(Long id,
                      LocalDate pickupDate,
                      LocalDate returnDate,
                      Long vehicleId,
                      Long renterId,
                      String status,
                      BigDecimal totalPrice,
                      String currency,
                      OffsetDateTime createdAt) {
        this.id = id;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.vehicleId = vehicleId;
        this.renterId = renterId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Long getRenterId() { return renterId; }
    public void setRenterId(Long renterId) { this.renterId = renterId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
