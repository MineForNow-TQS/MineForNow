package tqs.backend.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Importante: Mockar o repositório porque o controlador precisa dele
import tqs.backend.repository.VehicleRepository; 
import tqs.backend.model.Vehicle;
import java.util.Arrays;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VehicleRepository vehicleRepository;

    @Test
    @Requirement("SCRUM-49")
    void whenGetVehicles_thenReturnJsonArray() throws Exception {
        // Mock dos dados (porque agora usamos base de dados)
        Vehicle car1 = new Vehicle();
        car1.setBrand("Fiat");
        Vehicle car2 = new Vehicle();
        car2.setBrand("Tesla");
        Vehicle car3 = new Vehicle();
        car3.setBrand("Renault");

        given(vehicleRepository.findAll()).willReturn(Arrays.asList(car1, car2, car3));

        mvc.perform(get("/api/vehicles") // Endpoint atualizado
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].brand", is("Fiat")));
    }
}