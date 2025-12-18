package tqs.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpgradeOwnerRequest {

    @NotBlank
    private String phone;

    @NotBlank
    private String citizenCardNumber;

    @NotBlank
    @Pattern(
        regexp = "^[A-Z]{2}[0-9]{6}$",
        message = "Formato inválido da carta de condução"
    )
    private String drivingLicense;

    @NotBlank
    private String motivation;
}
