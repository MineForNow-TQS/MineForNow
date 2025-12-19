package tqs.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tqs.backend.dto.AdminStatsDTO;
import tqs.backend.dto.UpgradeOwnerRequest;
import tqs.backend.service.AdminService;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.crypto.password.PasswordEncoder;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;
import tqs.backend.service.UserService;

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
        private UserService userService;

        @MockBean
        private VehicleRepository vehicleRepository;

        @MockBean
        private UserRepository userRepository;

        @MockBean
        private BookingRepository bookingRepository;

        @MockBean
        private tqs.backend.repository.ReviewRepository reviewRepository;

        @MockBean
        private PasswordEncoder passwordEncoder;

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
                                                                                        // depending on config, usually
                                                                                        // 401
                                                                                        // or 403
                mockMvc.perform(get("/api/admin/stats"))
                                .andExpect(status().isUnauthorized());
        }

        @Nested
        @DisplayName("POST /api/admin/upgrade")
        class RequestOwnerUpgradeTests {
                @SuppressWarnings("null")
                @Test
                @WithMockUser(username = "test@test.com")
                @DisplayName("Should submit upgrade request successfully")
                void whenValidUpgradeRequest_thenReturns200() throws Exception {
                        UpgradeOwnerRequest upgradeRequest = new UpgradeOwnerRequest();
                        upgradeRequest.setPhone("+351912345678");
                        upgradeRequest.setCitizenCardNumber("12345678");
                        upgradeRequest.setDrivingLicense("AB123456");
                        upgradeRequest.setMotivation("Quero ser proprietário");

                        ObjectMapper objectMapper = new ObjectMapper();

                        mockMvc.perform(post("/api/admin/upgrade")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(upgradeRequest))
                                        .with(csrf()))
                                        .andExpect(status().isOk());

                        verify(userService)
                                        .requestOwnerUpgrade(eq("test@test.com"),
                                                        any(UpgradeOwnerRequest.class));
                }

                @SuppressWarnings("null")
                @Test
                @WithMockUser(username = "test@test.com")
                @DisplayName("Should return 400 when driving license format invalid")
                void whenInvalidDrivingLicense_thenReturns400() throws Exception {
                        UpgradeOwnerRequest upgradeRequest = new UpgradeOwnerRequest();
                        upgradeRequest.setPhone("+351912345678");
                        upgradeRequest.setCitizenCardNumber("12345678");
                        upgradeRequest.setDrivingLicense("123456"); // inválido
                        upgradeRequest.setMotivation("Motivação");

                        ObjectMapper objectMapper = new ObjectMapper();

                        mockMvc.perform(post("/api/admin/upgrade")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(upgradeRequest))
                                        .with(csrf()))
                                        .andExpect(status().isBadRequest());
                }

                @SuppressWarnings("null")
                @Test
                @WithMockUser(username = "owner@test.com")
                @DisplayName("Should return 409 when user already owner or pending")
                void whenAlreadyOwner_thenReturns409() throws Exception {
                        UpgradeOwnerRequest upgradeRequest = new UpgradeOwnerRequest();
                        upgradeRequest.setPhone("+351912345678");
                        upgradeRequest.setCitizenCardNumber("12345678");
                        upgradeRequest.setDrivingLicense("AB123456");
                        upgradeRequest.setMotivation("Motivação");

                        ObjectMapper objectMapper = new ObjectMapper();

                        doThrow(new IllegalStateException("Pedido já submetido ou utilizador já é Owner"))
                                        .when(userService).requestOwnerUpgrade(eq("owner@test.com"),
                                                        any(UpgradeOwnerRequest.class));

                        mockMvc.perform(post("/api/admin/upgrade")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(upgradeRequest))
                                        .with(csrf()))
                                        .andExpect(status().isConflict());
                }
        }
}
