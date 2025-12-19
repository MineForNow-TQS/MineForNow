package tqs.backend.controller;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import tqs.backend.dto.UpdateProfileRequest;
import tqs.backend.dto.UserProfileResponse;
import tqs.backend.model.UserRole;
import tqs.backend.security.JwtAuthenticationFilter;
import tqs.backend.security.JwtUtils;
import tqs.backend.security.UserDetailsServiceImpl;
import tqs.backend.service.UserService;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController Unit Tests")
@Requirement("SCRUM-46")
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private JwtUtils jwtUtils;

        @MockBean
        private UserDetailsServiceImpl userDetailsService;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        private ObjectMapper objectMapper;

        private UserProfileResponse mockProfile;

        @BeforeEach
        void setUp() {
                mockProfile = UserProfileResponse.builder()
                                .id(1L)
                                .fullName("Test User")
                                .email("test@test.com")
                                .phone("+351912345678")
                                .drivingLicense("AB123456")
                                .role(UserRole.RENTER)
                                .build();
        }

        @Nested
        @DisplayName("GET /api/users/me")
        class GetCurrentUserTests {

                @Test
                @WithMockUser(username = "test@test.com")
                @DisplayName("Should return user profile when authenticated")
                void whenGetMe_thenReturnsProfile() throws Exception {
                        when(userService.getUserProfile("test@test.com")).thenReturn(mockProfile);

                        mockMvc.perform(get("/api/users/me"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").value(1))
                                        .andExpect(jsonPath("$.fullName").value("Test User"))
                                        .andExpect(jsonPath("$.email").value("test@test.com"))
                                        .andExpect(jsonPath("$.phone").value("+351912345678"))
                                        .andExpect(jsonPath("$.drivingLicense").value("AB123456"))
                                        .andExpect(jsonPath("$.role").value("RENTER"));
                }

                @Test
                @WithMockUser(username = "unknown@test.com")
                @DisplayName("Should return 400 when user not found")
                void whenGetMeUserNotFound_thenReturns400() throws Exception {
                        when(userService.getUserProfile("unknown@test.com"))
                                        .thenThrow(new IllegalArgumentException("Utilizador não encontrado"));

                        mockMvc.perform(get("/api/users/me"))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.message").value("Utilizador não encontrado"));
                }
        }

        @Nested
        @DisplayName("PUT /api/users/me")
        class UpdateCurrentUserTests {

                @Test
                @WithMockUser(username = "test@test.com")
                @DisplayName("Should update profile successfully")
                void whenUpdateProfile_thenReturnsUpdatedProfile() throws Exception {
                        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                                        .phone("+351999888777")
                                        .drivingLicense("NEW12345")
                                        .build();

                        UserProfileResponse updatedProfile = UserProfileResponse.builder()
                                        .id(1L)
                                        .fullName("Test User")
                                        .email("test@test.com")
                                        .phone("+351999888777")
                                        .drivingLicense("NEW12345")
                                        .role(UserRole.RENTER)
                                        .build();

                        when(userService.updateUserProfile(eq("test@test.com"), any(UpdateProfileRequest.class)))
                                        .thenReturn(updatedProfile);

                        mockMvc.perform(put("/api/users/me")
                                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                                        .content(Objects.requireNonNull(
                                                        objectMapper.writeValueAsString(updateRequest))))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.phone").value("+351999888777"))
                                        .andExpect(jsonPath("$.drivingLicense").value("NEW12345"));
                }

                @Test
                @WithMockUser(username = "test@test.com")
                @DisplayName("Should update only phone when only phone provided")
                void whenUpdateOnlyPhone_thenReturnsUpdatedProfile() throws Exception {
                        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                                        .phone("+351111222333")
                                        .build();

                        UserProfileResponse updatedProfile = UserProfileResponse.builder()
                                        .id(1L)
                                        .fullName("Test User")
                                        .email("test@test.com")
                                        .phone("+351111222333")
                                        .drivingLicense("AB123456")
                                        .role(UserRole.RENTER)
                                        .build();

                        when(userService.updateUserProfile(eq("test@test.com"), any(UpdateProfileRequest.class)))
                                        .thenReturn(updatedProfile);

                        mockMvc.perform(put("/api/users/me")
                                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                                        .content(Objects.requireNonNull(
                                                        objectMapper.writeValueAsString(updateRequest))))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.phone").value("+351111222333"))
                                        .andExpect(jsonPath("$.drivingLicense").value("AB123456"));
                }

                @Test
                @WithMockUser(username = "unknown@test.com")
                @DisplayName("Should return 400 when user not found for update")
                void whenUpdateProfileUserNotFound_thenReturns400() throws Exception {
                        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                                        .phone("+351999888777")
                                        .build();

                        when(userService.updateUserProfile(eq("unknown@test.com"), any(UpdateProfileRequest.class)))
                                        .thenThrow(new IllegalArgumentException("Utilizador não encontrado"));

                        mockMvc.perform(put("/api/users/me")
                                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                                        .content(Objects.requireNonNull(
                                                        objectMapper.writeValueAsString(updateRequest))))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.message").value("Utilizador não encontrado"));
                }
        }
        @Nested
        @DisplayName("PUT /api/users/{id}/block.")
        class AdminManagementTests {

                @Test
                @WithMockUser(roles = "ADMIN")
                @DisplayName("Should toggle user status (block/unblock)")
                void whenToggleBlock_thenReturnsOk() throws Exception {
                        // Simula o sucesso da operação no serviço
                        mockMvc.perform(put("/api/users/1/block"))
                                        .andExpect(status().isOk());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                @DisplayName("Should return 400 when blocking non-existent user")
                void whenBlockUnknownUser_thenReturns400() throws Exception {
                        // Simula erro de utilizador não encontrado
                        org.mockito.Mockito.doThrow(new IllegalArgumentException("Utilizador não encontrado"))
                                        .when(userService).toggleUserStatus(99L);

                        mockMvc.perform(put("/api/users/99/block"))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.message").value("Utilizador não encontrado"));
                }

                
        }

}
