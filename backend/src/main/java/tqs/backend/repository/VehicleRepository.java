package tqs.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tqs.backend.model.Vehicle;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    /**
     * Pesquisa simples (apenas cidade), caso o utilizador não indique datas.
     */
    List<Vehicle> findByCityContainingIgnoreCase(String city);

    /**
     * Pesquisa completa (Cidade + Disponibilidade por intervalo de datas).
     * Nota: este método assume que startDate e endDate são não-null.
     */
    @Query("""
            SELECT v
            FROM Vehicle v
            WHERE LOWER(v.city) LIKE LOWER(CONCAT('%', :city, '%'))
              AND v.id NOT IN (
                    SELECT b.vehicle.id
                    FROM Booking b
                    WHERE (:startDate <= b.returnDate AND :endDate >= b.pickupDate)
              )
            """)
    List<Vehicle> findAvailableVehicles(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Pesquisa por disponibilidade (sem cidade).
     * Nota: este método assume que startDate e endDate são não-null.
     */
    @Query("""
            SELECT v
            FROM Vehicle v
            WHERE v.id NOT IN (
                    SELECT b.vehicle.id
                    FROM Booking b
                    WHERE (:startDate <= b.returnDate AND :endDate >= b.pickupDate)
            )
            """)
    List<Vehicle> findAvailableVehiclesByDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Buscar veículos por email do owner.
     */
    List<Vehicle> findByOwnerEmail(String email);

    /**
     * Pesquisa completa com filtros opcionais (preço, categoria/type, combustível),
     * mantendo a exclusão por bookings no intervalo.
     *
     * Regras importantes:
     * - Se não quiseres aplicar city/minPrice/maxPrice/categorias/combustiveis, passa null.
     * - Para coleções (categorias/combustiveis), passa null se estiver vazio (evita "IN ()").
     */
    @Query("""
            SELECT v
            FROM Vehicle v
            WHERE (:city IS NULL OR LOWER(v.city) LIKE LOWER(CONCAT('%', :city, '%')))
              AND (:minPrice IS NULL OR v.pricePerDay >= :minPrice)
              AND (:maxPrice IS NULL OR v.pricePerDay <= :maxPrice)
              AND (:categories IS NULL OR v.type IN :categories)
              AND (:fuelTypes IS NULL OR v.fuelType IN :fuelTypes)
              AND v.id NOT IN (
                    SELECT b.vehicle.id
                    FROM Booking b
                    WHERE (:startDate <= b.returnDate AND :endDate >= b.pickupDate)
              )
            """)
    List<Vehicle> searchAvailableWithFilters(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("categories") Collection<String> categories,
            @Param("fuelTypes") Collection<String> fuelTypes
    );
}
