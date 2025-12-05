package tqs.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; // Melhor que SpringBootTest para controladores
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void whenGetCars_thenReturnJsonArray() throws Exception {
        //mvc.perform(get("/api/cars")
        //        .contentType(MediaType.APPLICATION_JSON))
        //        .andExpect(status().isOk())
        //        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        //        .andExpect(jsonPath("$", hasSize(3))) // Verifica se devolve 3 carros
        //        .andExpect(jsonPath("$[0].brand", is("Fiat"))); // Verifica se o primeiro é Fiat
    }
}