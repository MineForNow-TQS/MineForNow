package tqs.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tqs.backend.model.Booking;
import tqs.backend.model.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

        @Query("SELECT COUNT(b) FROM Booking b WHERE b.vehicle.id = :vehicleId " +
                        "AND b.status != 'CANCELLED' " +
                        "AND (:startDate <= b.returnDate AND :endDate >= b.pickupDate)")
        long countOverlappingBookings(@Param("vehicleId") Long vehicleId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        List<Booking> findByRenter(User renter);

        List<Booking> findByVehicleId(Long vehicleId);
}