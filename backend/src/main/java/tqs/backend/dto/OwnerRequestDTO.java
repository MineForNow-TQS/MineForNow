package tqs.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tqs.backend.model.UserRole;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OwnerRequestDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String citizenCardNumber;
    private String drivingLicense;
    private String motivation;
    private UserRole role;
}