package tqs.backend.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tqs.backend.dto.OwnerRequestDTO;
import tqs.backend.security.JwtAuthenticationFilter;
import tqs.backend.security.JwtUtils;
import tqs.backend.security.UserDetailsServiceImpl;
import tqs.backend.service.AdminService;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva seguridad para el test unitario
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
        private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private tqs.backend.repository.VehicleRepository vehicleRepository;

    @MockBean
    private tqs.backend.repository.UserRepository userRepository;

    @Test
    void getPendingRequests_shouldReturnList() throws Exception {
        OwnerRequestDTO req = OwnerRequestDTO.builder()
                .id(1L)
                .fullName("Joao")
                .email("joao@test.com")
                .build();

        when(adminService.getPendingOwnerRequests()).thenReturn(List.of(req));

        mockMvc.perform(get("/api/admin/requests/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Joao"))
                .andExpect(jsonPath("$[0].email").value("joao@test.com"));
    }

    @Test
    void approveRequest_shouldReturnSuccess() throws Exception {
        mockMvc.perform(put("/api/admin/requests/1/approve")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}