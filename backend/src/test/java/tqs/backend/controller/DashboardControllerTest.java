package tqs.backend.controller;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
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
    @Requirement("SCRUM-24")
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
    @Requirement("SCRUM-24")
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
    @Requirement("SCRUM-24")
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
    @Requirement("SCRUM-24")
    void getOwnerDashboard_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/dashboard/owner"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Requirement("SCRUM-24")
    @WithMockUser(username = "owner@test.com", roles = "RENTER")
    void getOwnerDashboard_WrongRole() throws Exception {
        // When & Then - Should still work as long as authenticated
        // (Role check would be in security config if needed)
        when(dashboardService.getOwnerStats("owner@test.com")).thenReturn(mockStats);

        mockMvc.perform(get("/api/dashboard/owner"))
                .andExpect(status().isOk());
    }

    @Test
    @Requirement("SCRUM-24")
    @WithMockUser(username = "owner@test.com", roles = "OWNER")
    void getOwnerActiveBookings_Success() throws Exception {
        // Given
        var bookings = java.util.List.of(
                new tqs.backend.dto.BookingDTO(1L, java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(5),
                        "CONFIRMED", 500.0, 1L, 2L));
        when(dashboardService.getActiveBookings("owner@test.com")).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/dashboard/owner/active-bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @Requirement("SCRUM-24")
    @WithMockUser(username = "unknown@test.com", roles = "OWNER")
    void getOwnerActiveBookings_OwnerNotFound() throws Exception {
        // Given
        when(dashboardService.getActiveBookings("unknown@test.com"))
                .thenThrow(new IllegalArgumentException("Owner not found"));

        // When & Then
        mockMvc.perform(get("/api/dashboard/owner/active-bookings"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Owner not found"));
    }

    @Test
    @Requirement("SCRUM-24")
    void getOwnerActiveBookings_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/dashboard/owner/active-bookings"))
                .andExpect(status().isUnauthorized());
    }
}
