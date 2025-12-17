package tqs.backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate pickupDate;

    @Column(nullable = false)
    private LocalDate returnDate;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private String status; // WAITING_PAYMENT, CONFIRMED, CANCELLED

    @Column(nullable = false)
    private Double totalPrice;

    @ManyToOne
    @JoinColumn(name = "renter_id")
    private User renter;

    // SCRUM-16: Payment fields
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_method")
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD

    // No-arg constructor
    public Booking() {
    }

    // All-args constructor
    public Booking(Long id, java.time.LocalDate pickupDate, java.time.LocalDate returnDate, Vehicle vehicle,
            User renter, String status, Double totalPrice) {
        this.id = id;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.vehicle = vehicle;
        this.renter = renter;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    // Legacy constructor for backward compatibility (tests)
    public Booking(Long id, java.time.LocalDate pickupDate, java.time.LocalDate returnDate, Vehicle vehicle) {
        this(id, pickupDate, returnDate, vehicle, null, "CONFIRMED", 0.0);
    }

    // Additional Constructor for easier instantiation (without ID)
    public Booking(java.time.LocalDate pickupDate, java.time.LocalDate returnDate, Vehicle vehicle, User renter,
            String status, Double totalPrice) {
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.vehicle = vehicle;
        this.renter = renter;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.time.LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(java.time.LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public java.time.LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(java.time.LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public User getRenter() {
        return renter;
    }

    public void setRenter(User renter) {
        this.renter = renter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }<<<<<<<HEAD

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }=======>>>>>>>origin/feat/SCRUM-15-booking-intent

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", pickupDate=" + pickupDate +
                ", returnDate=" + returnDate +
                ", vehicle=" + (vehicle != null ? vehicle.getBrand() + " " + vehicle.getModel() : null) +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }
}