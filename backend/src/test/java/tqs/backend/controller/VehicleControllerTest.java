package tqs.backend.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;
import tqs.backend.service.VehicleService;

@WebMvcTest(VehicleController.class)
@ActiveProfiles("test")
class VehicleControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VehicleRepository vehicleRepository;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    private User testOwner;

    @BeforeEach
    void setUp() {
        testOwner = User.builder()
                .id(1L)
                .email("owner@test.com")
                .fullName("Test Owner")
                .role(UserRole.OWNER)
                .build();
    }

    @Test
    @Requirement("SCRUM-49") // História Principal
    void givenCityOnly_whenSearch_thenReturnsVehicles() throws Exception {
        Vehicle car = Vehicle.builder()
                .owner(testOwner)
                .brand("Ferrari")
                .city("Lisboa")
                .build();

        given(vehicleRepository.findByCityContainingIgnoreCase("Lisboa"))
                .willReturn(Arrays.asList(car));

        mvc.perform(get("/api/vehicles/search")
                .param("city", "Lisboa")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].brand", is("Ferrari")));
    }

    @Test
    @Requirement("SCRUM-49") // História Principal
    void givenDates_whenSearch_thenCallsAvailabilityService() throws Exception {
        Vehicle car = Vehicle.builder()
                .owner(testOwner)
                .brand("Tesla")
                .build();

        // Mock: Se pedirem datas, devolve o Tesla
        given(vehicleRepository.findAvailableVehicles(eq("Porto"), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(Arrays.asList(car));

        mvc.perform(get("/api/vehicles/search")
                .param("city", "Porto")
                .param("pickup", "2025-12-10")
                .param("dropoff", "2025-12-12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].brand", is("Tesla")));
    }
    
    @Test
    @Requirement("SCRUM-49") // História Principal
    void givenNoResults_thenReturnEmptyList() throws Exception {
        given(vehicleRepository.findByCityContainingIgnoreCase("Mars"))
                .willReturn(Collections.emptyList());

        mvc.perform(get("/api/vehicles/search")
                .param("city", "Mars")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Requirement("SCRUM-49")
    void givenNoFilters_whenSearch_thenReturnsAllVehicles() throws Exception {
        Vehicle car1 = Vehicle.builder()
                .owner(testOwner)
                .brand("Toyota")
                .city("Lisboa")
                .build();

        Vehicle car2 = Vehicle.builder()
                .owner(testOwner)
                .brand("Honda")
                .city("Porto")
                .build();

        given(vehicleRepository.findAll())
                .willReturn(Arrays.asList(car1, car2));

        mvc.perform(get("/api/vehicles/search")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].brand", is("Toyota")))
                .andExpect(jsonPath("$[1].brand", is("Honda")));
    }

    @Test
    @Requirement("SCRUM-49")
    void givenDatesOnly_whenSearch_thenReturnsAvailableVehicles() throws Exception {
        Vehicle car = Vehicle.builder()
                .owner(testOwner)
                .brand("Nissan")
                .model("Leaf")
                .build();

        given(vehicleRepository.findAvailableVehiclesByDates(any(LocalDate.class), any(LocalDate.class)))
                .willReturn(Arrays.asList(car));

        mvc.perform(get("/api/vehicles/search")
                .param("pickup", "2025-12-10")
                .param("dropoff", "2025-12-12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].brand", is("Nissan")));
    }

    @Test
    @Requirement("SCRUM-49")
    void givenGetAllVehicles_thenReturnsList() throws Exception {
        Vehicle car1 = Vehicle.builder()
                .owner(testOwner)
                .brand("Mercedes")
                .build();
        
        Vehicle car2 = Vehicle.builder()
                .owner(testOwner)
                .brand("BMW")
                .build();

        given(vehicleRepository.findAll())
                .willReturn(Arrays.asList(car1, car2));

        mvc.perform(get("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].brand", is("Mercedes")))
                .andExpect(jsonPath("$[1].brand", is("BMW")));
    }
}