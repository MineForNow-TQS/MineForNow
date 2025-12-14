package tqs.backend.mapper;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.stereotype.Component;

import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.model.Vehicle;

@Component
public class VehicleMapper {

    public VehicleDetailDTO toDetailDTO(Vehicle v) {
        if (v == null) return null;

        String ownerName = (v.getOwner() != null) ? v.getOwner().getFullName() : null;
        String ownerEmail = (v.getOwner() != null) ? v.getOwner().getEmail() : null;

        BigDecimal ppd = v.getPricePerDay();
        Double ppdDouble = (ppd != null) ? ppd.doubleValue() : null;

        return VehicleDetailDTO.builder()
                .id(v.getId())
                .brand(v.getBrand())
                .model(v.getModel())
                .year(v.getYear())
                .type(v.getType())
                .licensePlate(v.getLicensePlate())
                .mileage(v.getMileage())
                .fuelType(v.getFuelType())
                .transmission(v.getTransmission())
                .seats(v.getSeats())
                .doors(v.getDoors())
                .hasAC(Boolean.TRUE.equals(v.getHasAC()))
                .hasGPS(Boolean.TRUE.equals(v.getHasGPS()))
                .hasBluetooth(Boolean.TRUE.equals(v.getHasBluetooth()))
                .city(v.getCity())
                .exactLocation(v.getExactLocation())
                .pricePerDay(ppdDouble)
                .formattedPrice(formatEur(ppd, v.getCurrency()))
                .description(v.getDescription())
                .imageUrl(v.getImageUrl())
                .ownerName(ownerName)
                .ownerEmail(ownerEmail)
                .build();
    }

    private String formatEur(BigDecimal amount, String currency) {
        if (amount == null) return null;

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "PT"));

        String formatted = nf.format(amount);

        if (currency != null && !currency.isBlank() && !"EUR".equalsIgnoreCase(currency)) {
            return formatted + " " + currency.toUpperCase();
        }
        return formatted;
    }
}
