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
import static org.mockito.BDDMockito.willThrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import tqs.backend.config.SecurityConfig;
import tqs.backend.security.JwtUtils;
import tqs.backend.security.UserDetailsServiceImpl;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithAnonymousUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import tqs.backend.dto.CreateVehicleRequest;

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
@WithMockUser
@Import(SecurityConfig.class)
@SuppressWarnings("null")
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

        @MockBean
        private UserDetailsServiceImpl userDetailsService;

        @MockBean
        private JwtUtils jwtUtils;

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
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
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
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
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
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
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
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
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
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
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
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].brand", is("Mercedes")))
                                .andExpect(jsonPath("$[1].brand", is("BMW")));
        }

        @Test
        @WithAnonymousUser
        void whenCreateVehicleUnauthenticated_thenReturnUnauthorized() throws Exception {
                CreateVehicleRequest request = new CreateVehicleRequest();
                request.setBrand("Ferrari");

                mvc.perform(post("/api/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithAnonymousUser
        void whenGetMyVehiclesUnauthenticated_thenReturnUnauthorized() throws Exception {
                mvc.perform(get("/api/vehicles/my-vehicles")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithAnonymousUser
        void whenUpdateVehicleUnauthenticated_thenReturnUnauthorized() throws Exception {
                CreateVehicleRequest request = new CreateVehicleRequest();

                mvc.perform(put("/api/vehicles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithAnonymousUser
        void whenDeleteVehicleUnauthenticated_thenReturnUnauthorized() throws Exception {
                mvc.perform(delete("/api/vehicles/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @Requirement("SCRUM-7")
        void whenUpdateVehicleNotOwner_thenReturnForbidden() throws Exception {
                CreateVehicleRequest request = new CreateVehicleRequest();
                request.setBrand("Ferrari");
                request.setModel("Test");
                request.setYear(2022);
                request.setPricePerDay(100.0);
                request.setFuelType("Gasolina");
                request.setCity("Lisboa");
                request.setSeats(2);
                request.setDoors(2);
                request.setTransmission("Manual");
                request.setType("Desportivo");

                given(vehicleService.updateVehicle(eq(1L), any(CreateVehicleRequest.class), any()))
                                .willThrow(new IllegalArgumentException("Not owner"));

                mvc.perform(put("/api/vehicles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                                .andExpect(status().isForbidden());
        }

        @Test
        @Requirement("SCRUM-7")
        void whenDeleteVehicleNotOwner_thenReturnForbidden() throws Exception {
                willThrow(new IllegalArgumentException("Not owner")).given(vehicleService).deleteVehicle(eq(1L), any());

                mvc.perform(delete("/api/vehicles/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isForbidden());
        }

        @Test
        @Requirement("SCRUM-7")
        void whenCreateVehicleInvalid_thenReturnBadRequest() throws Exception {
                CreateVehicleRequest request = new CreateVehicleRequest();
                // Missing required fields triggers validation or service exception if manually
                // checked,
                // but controller has @Valid. If we want to test service exception:
                request.setBrand("Ferrari"); // valid enough to pass @Valid maybe? No, DTO has @NotBlank checks.
                // If we want to test Service throwing IllegalArgumentException (mimicking
                // business rule fail):
                request.setModel("Test");
                request.setYear(2022);
                request.setPricePerDay(10.0);
                request.setFuelType("Gas");
                request.setCity("Lisbon");
                // We need to bypass @Valid OR mock service to throw.
                // If we make a valid request structure but force service to throw:

                given(vehicleService.createVehicle(any(CreateVehicleRequest.class), any()))
                                .willThrow(new IllegalArgumentException("Bad vehicle"));

                mvc.perform(post("/api/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }
}