package tqs.backend.integration;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tqs.backend.model.Review;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.ReviewRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.time.LocalDateTime;

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

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        // Limpar dados de teste
        reviewRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        // Criar usuários de teste
        User owner = User.builder()
                .email("owner@test.com")
                .fullName("Owner Test")
                .password("password")
                .role(UserRole.OWNER)
                .build();
        owner = userRepository.save(owner);

        User renter = User.builder()
                .email("renter@test.com")
                .fullName("Renter Test")
                .password("password")
                .role(UserRole.RENTER)
                .build();
        renter = userRepository.save(renter);

        // Criar veículo de teste
        testVehicle = new Vehicle();
        testVehicle.setOwner(owner);
        testVehicle.setBrand("Mercedes-Benz");
        testVehicle.setModel("AMG GT");
        testVehicle.setYear(2021);
        testVehicle.setType("Desportivo");
        testVehicle.setLicensePlate("DD-04-DD");
        testVehicle.setMileage(18000);
        testVehicle.setFuelType("Gasolina");
        testVehicle.setTransmission("Automática");
        testVehicle.setSeats(2);
        testVehicle.setDoors(2);
        testVehicle.setHasAC(true);
        testVehicle.setHasGPS(true);
        testVehicle.setHasBluetooth(true);
        testVehicle.setCity("Lisboa");
        testVehicle.setExactLocation("Avenida da Liberdade");
        testVehicle.setPricePerDay(850.0);
        testVehicle.setDescription("Mercedes-AMG GT de luxo");
        testVehicle.setImageUrl("/Images/photo-1617814076367-b759c7d7e738.jpeg");
        testVehicle = vehicleRepository.save(testVehicle);

        // Criar 3 reviews para o veículo
        Review review1 = new Review();
        review1.setVehicle(testVehicle);
        review1.setReviewer(renter);
        review1.setRating(5);
        review1.setComment("Excelente carro! Experiência incrível de condução.");
        review1.setCreatedAt(LocalDateTime.now().minusDays(5));
        reviewRepository.save(review1);

        Review review2 = new Review();
        review2.setVehicle(testVehicle);
        review2.setReviewer(owner);
        review2.setRating(4);
        review2.setComment("Muito bom, mas um pouco caro para o meu orçamento.");
        review2.setCreatedAt(LocalDateTime.now().minusDays(2));
        reviewRepository.save(review2);

        Review review3 = new Review();
        review3.setVehicle(testVehicle);
        review3.setReviewer(renter);
        review3.setRating(5);
        review3.setComment("Perfeito! Recomendo a todos.");
        review3.setCreatedAt(LocalDateTime.now().minusDays(1));
        reviewRepository.save(review3);
    }

    @Test
    @Requirement("SCRUM-30")
    void getVehicleReviews_EndToEnd_Success() throws Exception {
        // When & Then - Test vehicle has 3 reviews
        mockMvc.perform(get("/api/vehicles/" + testVehicle.getId() + "/reviews"))
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
