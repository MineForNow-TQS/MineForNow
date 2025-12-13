package tqs.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import tqs.backend.dto.LoginRequest;
import tqs.backend.dto.RegisterRequest;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.security.JwtAuthenticationFilter;
import tqs.backend.security.JwtUtils;
import tqs.backend.security.UserDetailsServiceImpl;
import tqs.backend.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthenticationManager authenticationManager;

        @MockBean
        private JwtUtils jwtUtils;

        @MockBean
        private UserDetailsServiceImpl userDetailsService;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @MockBean
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        private RegisterRequest validRequest;
        private RegisterRequest passwordMismatchRequest;

        @BeforeEach
        void setUp() {
                validRequest = new RegisterRequest();
                validRequest.setFullName("João Silva");
                validRequest.setEmail("joao@email.com");
                validRequest.setPassword("Senha123");
                validRequest.setConfirmPassword("Senha123");

                passwordMismatchRequest = new RegisterRequest();
                passwordMismatchRequest.setFullName("Maria");
                passwordMismatchRequest.setEmail("maria@email.com");
                passwordMismatchRequest.setPassword("Senha123");
                passwordMismatchRequest.setConfirmPassword("Senha321");
        }

        @Test
        @Requirement("SCRUM-32")
        @DisplayName("POST /login - Success")
        void whenPostLoginValid_thenReturns200AndToken() throws Exception {
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail("user@test.com");
                loginRequest.setPassword("password");

                when(authenticationManager.authenticate(any(Authentication.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn("mock-jwt-token");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                                .andExpect(jsonPath("$.type").value("Bearer"));
        }

        @Test
        @Requirement("SCRUM-32")
        @DisplayName("POST /logout - Success")
        void whenPostLogout_thenReturns200() throws Exception {
                mockMvc.perform(post("/api/auth/logout"))
                                .andExpect(status().isOk());
        }

        @Test
        @Requirement("SCRUM-37")
        void registerShouldReturn200WhenSuccess() throws Exception {
                User mockUser = User.builder()
                                .id(1L)
                                .fullName(validRequest.getFullName())
                                .email(validRequest.getEmail())
                                .role(UserRole.RENTER)
                                .build();

                when(userService.register(any(RegisterRequest.class))).thenReturn(mockUser);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Conta criada com sucesso"))
                                .andExpect(jsonPath("$.userId").value(1))
                                .andExpect(jsonPath("$.email").value("joao@email.com"))
                                .andExpect(jsonPath("$.role").value("RENTER"));
        }

        @Test
        @Requirement("SCRUM-37")
        void registerShouldReturn400WhenPasswordsDoNotMatch() throws Exception {
                when(userService.register(any(RegisterRequest.class)))
                                .thenThrow(new IllegalArgumentException("As passwords não coincidem"));

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(passwordMismatchRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("As passwords não coincidem"));
        }

        @Test
        @Requirement("SCRUM-32")
        @DisplayName("POST /login - Invalid Credentials")
        void whenPostLoginInvalid_thenReturns401() throws Exception {
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail("user@test.com");
                loginRequest.setPassword("wrongpassword");

                when(authenticationManager.authenticate(any(Authentication.class)))
                                .thenThrow(new org.springframework.security.authentication.BadCredentialsException(
                                                "Bad credentials"));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @Requirement("SCRUM-40")
        @DisplayName("POST /register - Invalid Email")
        void whenPostRegisterInvalidEmail_thenReturns400() throws Exception {
                RegisterRequest invalidEmailRequest = new RegisterRequest();
                invalidEmailRequest.setFullName("João Silva");
                invalidEmailRequest.setEmail("invalid-email");
                invalidEmailRequest.setPassword("Senha123");
                invalidEmailRequest.setConfirmPassword("Senha123");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Email inválido"));
        }

        @Test
        @Requirement("SCRUM-40")
        @DisplayName("POST /register - Weak Password")
        void whenPostRegisterWeakPassword_thenReturns400() throws Exception {
                RegisterRequest weakPasswordRequest = new RegisterRequest();
                weakPasswordRequest.setFullName("João Silva");
                weakPasswordRequest.setEmail("joao@email.com");
                weakPasswordRequest.setPassword("weak");
                weakPasswordRequest.setConfirmPassword("weak");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(weakPasswordRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(
                                                "A password deve ter pelo menos 8 caracteres, 1 maiúscula, 1 minúscula e 1 número"));
        }
}
