package tqs.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renter_user_id", nullable = false)
    private User renter;

    @Column(name = "start_datetime", nullable = false)
    private OffsetDateTime startDateTime;

    @Column(name = "end_datetime", nullable = false)
    private OffsetDateTime endDateTime;

    @Column(nullable = false)
    private String status;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}