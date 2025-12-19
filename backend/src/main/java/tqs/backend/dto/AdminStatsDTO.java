package tqs.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsDTO {
    private long totalUsers;
    private long totalCars;
    private long totalBookings;
    private Double totalRevenue;
}
