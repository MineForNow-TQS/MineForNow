package tqs.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tqs.backend.dto.DashboardStatsDTO;
import tqs.backend.service.DashboardService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    private DashboardStatsDTO mockStats;

    @BeforeEach
    void setUp() {
        mockStats = new DashboardStatsDTO(
                5000.0, // totalRevenue
                10, // activeVehicles
                3, // pendingBookings
                25 // completedBookings
        );
    }

    @Test
    @WithMockUser(username = "owner@test.com", roles = "OWNER")
    void getOwnerDashboard_Success() throws Exception {
        // Given
        when(dashboardService.getOwnerStats("owner@test.com")).thenReturn(mockStats);

        // When & Then
        mockMvc.perform(get("/api/dashboard/owner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(5000.0))
                .andExpect(jsonPath("$.activeVehicles").value(10))
                .andExpect(jsonPath("$.pendingBookings").value(3))
                .andExpect(jsonPath("$.completedBookings").value(25));
    }

    @Test
    @WithMockUser(username = "owner@test.com", roles = "OWNER")
    void getOwnerDashboard_NoVehicles() throws Exception {
        // Given
        DashboardStatsDTO emptyStats = new DashboardStatsDTO(0.0, 0, 0, 0);
        when(dashboardService.getOwnerStats("owner@test.com")).thenReturn(emptyStats);

        // When & Then
        mockMvc.perform(get("/api/dashboard/owner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(0.0))
                .andExpect(jsonPath("$.activeVehicles").value(0))
                .andExpect(jsonPath("$.pendingBookings").value(0))
                .andExpect(jsonPath("$.completedBookings").value(0));
    }

    @Test
    @WithMockUser(username = "unknown@test.com", roles = "OWNER")
    void getOwnerDashboard_OwnerNotFound() throws Exception {
        // Given
        when(dashboardService.getOwnerStats("unknown@test.com"))
                .thenThrow(new IllegalArgumentException("Owner not found"));

        // When & Then
        mockMvc.perform(get("/api/dashboard/owner"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Owner not found"));
    }

    @Test
    void getOwnerDashboard_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/dashboard/owner"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "owner@test.com", roles = "RENTER")
    void getOwnerDashboard_WrongRole() throws Exception {
        // When & Then - Should still work as long as authenticated
        // (Role check would be in security config if needed)
        when(dashboardService.getOwnerStats("owner@test.com")).thenReturn(mockStats);

        mockMvc.perform(get("/api/dashboard/owner"))
                .andExpect(status().isOk());
    }
}
