package tqs.backend.integration;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Requirement("SCRUM-30")
    void getVehicleReviews_EndToEnd_Success() throws Exception {
        // When & Then - Vehicle 4 (Mercedes) has 3 reviews
        mockMvc.perform(get("/api/vehicles/4/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").exists())
                .andExpect(jsonPath("$.totalReviews").value(3))
                .andExpect(jsonPath("$.reviews").isArray())
                .andExpect(jsonPath("$.reviews.length()").value(3));
    }

    @Test
    @Requirement("SCRUM-30")
    void getVehicleReviews_VehicleNotFound_Returns404() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/vehicles/999/reviews"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Vehicle not found"));
    }
}
