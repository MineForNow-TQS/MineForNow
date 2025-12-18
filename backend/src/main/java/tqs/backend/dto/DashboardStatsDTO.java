package tqs.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Double totalRevenue;
    private Integer activeVehicles;
    private Integer pendingBookings;
    private Integer completedBookings;
}
