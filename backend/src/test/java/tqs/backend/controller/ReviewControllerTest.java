package tqs.backend.controller;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tqs.backend.dto.ReviewDTO;
import tqs.backend.dto.VehicleReviewsDTO;
import tqs.backend.service.ReviewService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ReviewService reviewService;

        private VehicleReviewsDTO mockReviewsDTO;

        @BeforeEach
        void setUp() {
                ReviewDTO review1 = new ReviewDTO(1L, "João Silva", 5, "Excelente!", LocalDateTime.now());
                ReviewDTO review2 = new ReviewDTO(2L, "Maria Santos", 4, "Muito bom", LocalDateTime.now());

                mockReviewsDTO = new VehicleReviewsDTO(4.5, 2L, Arrays.asList(review1, review2));
        }

        @Test
        @Requirement("SCRUM-30")
        void getVehicleReviews_ValidVehicleId_Returns200() throws Exception {
                // Given
                when(reviewService.getVehicleReviews(1L)).thenReturn(mockReviewsDTO);

                // When & Then
                mockMvc.perform(get("/api/vehicles/1/reviews"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.averageRating").value(4.5))
                                .andExpect(jsonPath("$.totalReviews").value(2))
                                .andExpect(jsonPath("$.reviews").isArray())
                                .andExpect(jsonPath("$.reviews[0].reviewerName").value("João Silva"));
        }

        @Test
        @Requirement("SCRUM-30")
        void getVehicleReviews_InvalidVehicleId_Returns404() throws Exception {
                // Given
                when(reviewService.getVehicleReviews(999L))
                                .thenThrow(new IllegalArgumentException("Vehicle not found"));

                // When & Then
                mockMvc.perform(get("/api/vehicles/999/reviews"))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("Vehicle not found"));
        }

        @Test
        @Requirement("SCRUM-30")
        void getVehicleReviews_WithReviews_ReturnsCorrectStructure() throws Exception {
                // Given
                when(reviewService.getVehicleReviews(1L)).thenReturn(mockReviewsDTO);

                // When & Then
                mockMvc.perform(get("/api/vehicles/1/reviews"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.averageRating").exists())
                                .andExpect(jsonPath("$.totalReviews").exists())
                                .andExpect(jsonPath("$.reviews").exists())
                                .andExpect(jsonPath("$.reviews[0].id").exists())
                                .andExpect(jsonPath("$.reviews[0].reviewerName").exists())
                                .andExpect(jsonPath("$.reviews[0].rating").exists())
                                .andExpect(jsonPath("$.reviews[0].comment").exists())
                                .andExpect(jsonPath("$.reviews[0].createdAt").exists());
        }

        @Test
        @Requirement("SCRUM-30")
        void getVehicleReviews_NoReviews_ReturnsEmptyList() throws Exception {
                // Given
                VehicleReviewsDTO emptyReviews = new VehicleReviewsDTO(0.0, 0L, Collections.emptyList());
                when(reviewService.getVehicleReviews(1L)).thenReturn(emptyReviews);

                // When & Then
                mockMvc.perform(get("/api/vehicles/1/reviews"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.averageRating").value(0.0))
                                .andExpect(jsonPath("$.totalReviews").value(0))
                                .andExpect(jsonPath("$.reviews").isEmpty());
        }

        @Test
        @Requirement("SCRUM-28")
        @org.springframework.security.test.context.support.WithMockUser(username = "renter@example.com")
        void createReview_ValidData_Returns201() throws Exception {
                // Given
                tqs.backend.dto.CreateReviewDTO createDTO = new tqs.backend.dto.CreateReviewDTO(5, "Great!", 1L);
                ReviewDTO createdReview = new ReviewDTO(1L, "Renter Name", 5, "Great!", LocalDateTime.now());

                when(reviewService.createReview(org.mockito.ArgumentMatchers.any(),
                                org.mockito.ArgumentMatchers.eq("renter@example.com")))
                                .thenReturn(createdReview);

                // When & Then
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/reviews")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.rating").value(5))
                                .andExpect(jsonPath("$.comment").value("Great!"));
        }

        @Test
        @Requirement("SCRUM-28")
        @org.springframework.security.test.context.support.WithMockUser(username = "renter@example.com")
        void createReview_InvalidBooking_Returns400() throws Exception {
                // Given
                tqs.backend.dto.CreateReviewDTO createDTO = new tqs.backend.dto.CreateReviewDTO(5, "Great!", 1L);

                when(reviewService.createReview(org.mockito.ArgumentMatchers.any(),
                                org.mockito.ArgumentMatchers.anyString()))
                                .thenThrow(new IllegalArgumentException("Invalid booking"));

                // When & Then
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/reviews")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Invalid booking"));
        }

        @Test
        @Requirement("SCRUM-28")
        @org.springframework.security.test.context.support.WithMockUser(username = "renter@example.com")
        void createReview_ConflictState_Returns409() throws Exception {
                // Given
                tqs.backend.dto.CreateReviewDTO createDTO = new tqs.backend.dto.CreateReviewDTO(5, "Great!", 1L);

                when(reviewService.createReview(org.mockito.ArgumentMatchers.any(),
                                org.mockito.ArgumentMatchers.anyString()))
                                .thenThrow(new IllegalStateException("Booking not completed"));

                // When & Then
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/reviews")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDTO)))
                                .andExpect(status().isConflict())
                                .andExpect(content().string("Booking not completed"));
        }

        @Test
        @Requirement("SCRUM-30")
        void getVehicleReviews_InternalError_Returns500() throws Exception {
                // Given
                when(reviewService.getVehicleReviews(org.mockito.ArgumentMatchers.anyLong()))
                                .thenThrow(new RuntimeException("Database error"));

                // When & Then
                mockMvc.perform(get("/api/vehicles/1/reviews"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().string("Internal server error"));
        }
}
