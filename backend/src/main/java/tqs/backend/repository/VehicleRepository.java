package tqs.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tqs.backend.model.Vehicle;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

       // Pesquisa simples (apenas cidade), caso o utilizador não meta datas
       List<Vehicle> findByCityContainingIgnoreCase(String city);

       // Pesquisa completa (Cidade + Disponibilidade de Datas)
       @Query("SELECT v FROM Vehicle v WHERE " +
                     "LOWER(v.city) LIKE LOWER(CONCAT('%', :city, '%')) " +
                     "AND v.id NOT IN (" +
                     "    SELECT b.vehicle.id FROM Booking b WHERE " +
                     "    (:startDate <= b.returnDate AND :endDate >= b.pickupDate)" +
                     ")")
       List<Vehicle> findAvailableVehicles(
                     @Param("city") String city,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       @Query("SELECT v FROM Vehicle v WHERE " +
                     "v.id NOT IN (" +
                     "    SELECT b.vehicle.id FROM Booking b WHERE " +
                     "    (:startDate <= b.returnDate AND :endDate >= b.pickupDate)" +
                     ")")
       List<Vehicle> findAvailableVehiclesByDates(
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Buscar veículos por email do owner
       List<Vehicle> findByOwnerEmail(String email);
}