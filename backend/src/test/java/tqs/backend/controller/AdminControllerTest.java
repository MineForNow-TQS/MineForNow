package tqs.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tqs.backend.dto.AdminStatsDTO;
import tqs.backend.service.AdminService;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(AdminControllerTest.TestSecurityConfig.class)
class AdminControllerTest {

    @TestConfiguration
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .exceptionHandling(e -> e.authenticationEntryPoint(
                            new org.springframework.security.web.authentication.HttpStatusEntryPoint(
                                    org.springframework.http.HttpStatus.UNAUTHORIZED)));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private tqs.backend.repository.VehicleRepository vehicleRepository;

    @MockBean
    private tqs.backend.repository.UserRepository userRepository;

    @MockBean
    private tqs.backend.repository.BookingRepository bookingRepository;

    @MockBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(roles = "ADMIN")
    @Requirement("SCRUM-75")
    void whenGetMetadata_thenReturnAdminStats() throws Exception {
        AdminStatsDTO stats = new AdminStatsDTO(10L, 5L, 20L, 1500.0);
        when(adminService.getDashboardStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(10))
                .andExpect(jsonPath("$.totalCars").value(5))
                .andExpect(jsonPath("$.totalBookings").value(20))
                .andExpect(jsonPath("$.totalRevenue").value(1500.0));
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenUserGetMetadata_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenUnauthenticatedGetMetadata_thenReturnUnauthorized() throws Exception { // Or Forbidden/Unauthorized
                                                                                    // depending on config, usually 401
                                                                                    // or 403
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isUnauthorized());
    }
}
