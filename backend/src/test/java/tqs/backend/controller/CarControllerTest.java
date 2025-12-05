package tqs.backend.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test; // Melhor que SpringBootTest para controladores
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @Requirement("SCRUM-49")
    void whenGetCars_thenReturnJsonArray() throws Exception {
        mvc.perform(get("/api/cars")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3))) // Verifica se devolve 3 carros
                .andExpect(jsonPath("$[0].brand", is("Fiat"))); // Verifica se o primeiro é Fiat
    }
}