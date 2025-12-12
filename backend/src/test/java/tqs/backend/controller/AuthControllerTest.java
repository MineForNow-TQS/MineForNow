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
import tqs.backend.security.JwtAuthenticationFilter;
import tqs.backend.security.JwtUtils;
import tqs.backend.security.UserDetailsServiceImpl;

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

    @Autowired
    private ObjectMapper objectMapper;

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
}
