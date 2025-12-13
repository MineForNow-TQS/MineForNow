package tqs.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import tqs.backend.dto.RegisterRequest;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

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
    @Requirement("SCRUM-40")
    void registerShouldReturn200WhenSuccess() throws Exception {
        User mockUser = User.builder()
                .id(1L)
                .fullName(validRequest.getFullName())
                .email(validRequest.getEmail())
                .role(UserRole.RENTER)
                .build();

        when(userService.register(validRequest)).thenReturn(mockUser);

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Conta criada com sucesso"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.role").value("RENTER"));
    }

    @Test
    @Requirement("SCRUM-40")
    void registerShouldReturn400WhenPasswordsDoNotMatch() throws Exception {
        when(userService.register(passwordMismatchRequest))
                .thenThrow(new IllegalArgumentException("As passwords não coincidem"));

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordMismatchRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("As passwords não coincidem"));
    }
}
