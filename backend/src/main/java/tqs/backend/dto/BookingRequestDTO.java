package tqs.backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingRequestDTO {

    @NotNull
    private Long vehicleId;

    @NotNull
    @FutureOrPresent
    private LocalDate startDate;

    @NotNull
    @FutureOrPresent
    private LocalDate endDate;

    @NotNull
    private Long renterId;

    public BookingRequestDTO() {
    }

    public BookingRequestDTO(Long vehicleId, LocalDate startDate, LocalDate endDate, Long renterId) {
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.renterId = renterId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }
}
