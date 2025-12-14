package tqs.backend.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tqs.backend.model.Booking;
import tqs.backend.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByCityContainingIgnoreCase(String city);

    @Query("""
        SELECT v
        FROM Vehicle v
        WHERE v.id NOT IN (
            SELECT b.vehicle.id
            FROM Booking b
            WHERE (b.startDateTime <= :endDateTime AND b.endDateTime >= :startDateTime)
        )
        """)
    List<Vehicle> findAvailableVehiclesByDateTime(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * Método que o VehicleController espera
     * Converte LocalDate -> intervalo LocalDateTime.
     */
    default List<Vehicle> findAvailableVehiclesByDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate e endDate não podem ser null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate não pode ser antes de startDate");
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        return findAvailableVehiclesByDateTime(start, end);
    }

    default List<Vehicle> findAvailableVehicles(String city, LocalDate startDate, LocalDate endDate) {
        if (city == null || city.isBlank()) {
            return findAvailableVehiclesByDates(startDate, endDate);
        }

        List<Vehicle> inCity = findByCityContainingIgnoreCase(city);

        List<Vehicle> available = findAvailableVehiclesByDates(startDate, endDate);

        return inCity.stream()
                .filter(v -> available.stream().anyMatch(av -> av.getId().equals(v.getId())))
                .toList();
    }
}
