package tqs.backend.mapper;

import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.model.Vehicle;

import java.text.DecimalFormat;

/**
 * Mapper para converter entre a entidade Vehicle e o DTO VehicleDetailDTO.
 * Implementa lógica de formatação e campos calculados.
 */
public class VehicleMapper {

    private VehicleMapper() {
        throw new IllegalStateException("Utility class");
    }

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#0.00");

    /**
     * Converte uma entidade Vehicle para VehicleDetailDTO.
     * 
     * @param vehicle entidade a converter
     * @return DTO com dados formatados, ou null se vehicle for null
     */
    public static VehicleDetailDTO toDetailDTO(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleDetailDTO.builder()
                .id(vehicle.getId())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .type(vehicle.getType())
                .licensePlate(vehicle.getLicensePlate())
                .mileage(vehicle.getMileage())
                .fuelType(vehicle.getFuelType())
                .transmission(vehicle.getTransmission())
                .seats(vehicle.getSeats())
                .doors(vehicle.getDoors())
                .hasAC(vehicle.getHasAC())
                .hasGPS(vehicle.getHasGPS())
                .hasBluetooth(vehicle.getHasBluetooth())
                .city(vehicle.getCity())
                .exactLocation(vehicle.getExactLocation())
                .pricePerDay(vehicle.getPricePerDay())
                .description(vehicle.getDescription())
                .imageUrl(vehicle.getImageUrl())
                .displayName(formatDisplayName(vehicle))
                .formattedPrice(formatPrice(vehicle.getPricePerDay()))
                .ownerName(vehicle.getOwner() != null ? vehicle.getOwner().getName() : null)
                .ownerEmail(vehicle.getOwner() != null ? vehicle.getOwner().getEmail() : null)
                .build();
    }

    /**
     * Formata o nome de exibição do veículo.
     * Formato: "Marca Modelo Ano" (ex: "Fiat 500 2020")
     */
    private static String formatDisplayName(Vehicle vehicle) {
        StringBuilder name = new StringBuilder();

        if (vehicle.getBrand() != null) {
            name.append(vehicle.getBrand());
        }

        if (vehicle.getModel() != null) {
            if (!name.isEmpty())
                name.append(" ");
            name.append(vehicle.getModel());
        }

        if (vehicle.getYear() != null) {
            if (!name.isEmpty())
                name.append(" ");
            name.append(vehicle.getYear());
        }

        return name.toString();
    }

    /**
     * Formata o preço para exibição.
     * Formato: "XX.XX €/dia" (ex: "25.00 €/dia")
     */
    private static String formatPrice(Double price) {
        if (price == null) {
            return "N/A";
        }
        return PRICE_FORMAT.format(price) + " €/dia";
    }
}
