package tqs.backend.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        // ✅ Campos calculados esperados pelos testes
        String displayName = buildDisplayName(v.getBrand(), v.getModel(), v.getYear());
        String formattedPrice = formatPricePerDay(ppd, v.getCurrency());

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

                // ✅ IMPORTANTE: preencher estes 2
                .displayName(displayName)
                .formattedPrice(formattedPrice)

                .description(v.getDescription())
                .imageUrl(v.getImageUrl())
                .ownerName(ownerName)
                .ownerEmail(ownerEmail)
                .build();
    }

    // "Fiat 500 2020" ou "Fiat 500" se year == null
    private String buildDisplayName(String brand, String model, Integer year) {
        String b = safeTrim(brand);
        String m = safeTrim(model);

        String base = (b + " " + m).trim();
        if (year == null) return base;

        return (base + " " + year).trim();
    }

    /**
     * Formato esperado pelos teus testes:
     * - se null -> "N/A"
     * - se EUR -> "25.00 €/dia"
     *
     * Nota: não uses NumberFormat PT-PT aqui porque dá "25,00 €" (vírgula + símbolo antes/depois)
     * e isso não bate certo com o teu assert.
     */
    private String formatPricePerDay(BigDecimal amount, String currency) {
        if (amount == null) return "N/A";

        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
        String value = String.format(Locale.US, "%.2f", scaled.doubleValue());

        String curr = (currency == null || currency.isBlank()) ? "EUR" : currency.trim().toUpperCase();

        if ("EUR".equals(curr)) {
            return value + " €/dia";
        }
        return value + " " + curr + "/dia";
    }

    private String safeTrim(String s) {
        return (s == null) ? "" : s.trim();
    }
}
