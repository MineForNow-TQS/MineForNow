package tqs.backend.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.nullable;

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
                given(vehicleService.searchVehicles(
                                eq("Porto"),
                                any(LocalDate.class),
                                any(LocalDate.class),
                                nullable(Double.class),
                                nullable(Double.class),
                                nullable(List.class),
                                nullable(List.class)))
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

                given(vehicleService.searchVehicles(
                                nullable(String.class),
                                any(LocalDate.class),
                                any(LocalDate.class),
                                nullable(Double.class),
                                nullable(Double.class),
                                nullable(List.class),
                                nullable(List.class)))
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

        @Test
        void givenVehicles_whenSearchVehiclesWithCombinedFiltersCsv_thenCallsServiceWithNormalizedLists() throws Exception {
                Vehicle v1 = Vehicle.builder().id(1L).brand("Fiat").model("500").build();
                List<Vehicle> vehicles = List.of(v1);

                given(vehicleService.searchVehicles(
                        eq("Lisboa"),
                        eq(LocalDate.parse("2025-12-20")),
                        eq(LocalDate.parse("2025-12-22")),
                        eq(40.0),
                        eq(100.0),
                        anyList(),
                        anyList()
                )).willReturn(vehicles);

                mvc.perform(get("/api/vehicles/search")
                                .param("city", "Lisboa")
                                .param("pickup", "2025-12-20")
                                .param("dropoff", "2025-12-22")
                                .param("minPrice", "40")
                                .param("maxPrice", "100")
                                .param("categories", "SUV,Citadino")
                                .param("fuelTypes", "Gasolina,Elétrico")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0].id", is(1)));

                ArgumentCaptor<List<String>> categoriesCaptor = ArgumentCaptor.forClass(List.class);
                ArgumentCaptor<List<String>> fuelTypesCaptor = ArgumentCaptor.forClass(List.class);

                verify(vehicleService).searchVehicles(
                        eq("Lisboa"),
                        eq(LocalDate.parse("2025-12-20")),
                        eq(LocalDate.parse("2025-12-22")),
                        eq(40.0),
                        eq(100.0),
                        categoriesCaptor.capture(),
                        fuelTypesCaptor.capture()
                );

                // normalização esperada
                org.junit.jupiter.api.Assertions.assertEquals(List.of("SUV", "Citadino"), categoriesCaptor.getValue());
                org.junit.jupiter.api.Assertions.assertEquals(List.of("Gasolina", "Elétrico"), fuelTypesCaptor.getValue());
        }

        @Test
        void givenVehicles_whenSearchVehiclesWithCombinedFiltersRepeatedParams_thenCallsServiceWithNormalizedLists() throws Exception {
                Vehicle v1 = Vehicle.builder().id(2L).brand("Nissan").model("Juke").build();
                List<Vehicle> vehicles = List.of(v1);

                given(vehicleService.searchVehicles(
                        eq(null),
                        eq(LocalDate.parse("2025-12-20")),
                        eq(LocalDate.parse("2025-12-22")),
                        eq(null),
                        eq(null),
                        anyList(),
                        anyList()
                )).willReturn(vehicles);

                mvc.perform(get("/api/vehicles/search")
                                .param("pickup", "2025-12-20")
                                .param("dropoff", "2025-12-22")
                                .param("categories", "SUV")
                                .param("categories", "Citadino")
                                .param("fuelTypes", "Gasolina")
                                .param("fuelTypes", "Elétrico")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0].id", is(2)));

                ArgumentCaptor<List<String>> categoriesCaptor = ArgumentCaptor.forClass(List.class);
                ArgumentCaptor<List<String>> fuelTypesCaptor = ArgumentCaptor.forClass(List.class);

                verify(vehicleService).searchVehicles(
                        eq(null),
                        eq(LocalDate.parse("2025-12-20")),
                        eq(LocalDate.parse("2025-12-22")),
                        eq(null),
                        eq(null),
                        categoriesCaptor.capture(),
                        fuelTypesCaptor.capture()
                );

                org.junit.jupiter.api.Assertions.assertEquals(List.of("SUV", "Citadino"), categoriesCaptor.getValue());
                org.junit.jupiter.api.Assertions.assertEquals(List.of("Gasolina", "Elétrico"), fuelTypesCaptor.getValue());
        }

}