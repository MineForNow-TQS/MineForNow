package tqs.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "renter_user_id", nullable = false)
    private User renter;

    @Column(name = "start_datetime", nullable = false)
    private OffsetDateTime startDateTime;

    @Column(name = "end_datetime", nullable = false)
    private OffsetDateTime endDateTime;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Payment fields (do fluxo de pagamento) — ainda não estão no V1__schema.sql.
     * Mantemos @Transient por agora para compilar sem rebentar com Flyway.
     */
    @Transient
    private LocalDateTime paymentDate;

    @Transient
    private String paymentMethod;

    /**
     * Convenience constructor usado pelos testes.
     */
    public Booking(Long id,
                   Vehicle vehicle,
                   User renter,
                   OffsetDateTime startDateTime,
                   OffsetDateTime endDateTime,
                   String status,
                   BigDecimal totalPrice,
                   String currency,
                   OffsetDateTime createdAt) {
        this.id = id;
        this.vehicle = vehicle;
        this.renter = renter;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    /* -----------------------------------------------------------------------
     * Compatibilidade com código antigo que ainda usa LocalDate.
     * NÃO são colunas: derivam de TIMESTAMPTZ.
     * --------------------------------------------------------------------- */

    @Transient
    public LocalDate getPickupDate() {
        return startDateTime != null ? startDateTime.toLocalDate() : null;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.startDateTime = pickupDate != null ? pickupDate.atStartOfDay().atOffset(ZoneOffset.UTC) : null;
    }

    @Transient
    public LocalDate getReturnDate() {
        return endDateTime != null ? endDateTime.toLocalDate() : null;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.endDateTime = returnDate != null ? returnDate.atStartOfDay().atOffset(ZoneOffset.UTC) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        return id != null && Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", vehicleId=" + (vehicle != null ? vehicle.getId() : null) +
                ", renterId=" + (renter != null ? renter.getId() : null) +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                ", currency='" + currency + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
