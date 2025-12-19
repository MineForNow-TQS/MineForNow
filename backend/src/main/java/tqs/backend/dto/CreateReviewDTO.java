package tqs.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewDTO {

    @NotNull(message = "O rating é obrigatório")
    @Min(value = 1, message = "O rating deve ser no mínimo 1")
    @Max(value = 5, message = "O rating deve ser no máximo 5")
    private Integer rating;

    private String comment; // Opcional

    @NotNull(message = "O ID da reserva é obrigatório")
    private Long bookingId;
}
