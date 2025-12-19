package tqs.backend.cucumber;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import io.cucumber.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerDashboardSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private String authToken;
    private ResponseEntity<Map<String, Object>> dashboardResponse;

    @SuppressWarnings("null")
    @Dado("que existe um owner autenticado com email {string}")
    public void queExisteUmOwnerAutenticadoComEmail(String email) {
        // Use existing owner from TestDataInitializer
        owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Owner not found: " + email));

        // Login to get token
        Map<String, String> loginRequest = Map.of(
                "email", email,
                "password", "password123");

        ResponseEntity<Map<String, Object>> loginResponse = restTemplate.exchange(
                "/api/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                new ParameterizedTypeReference<Map<String, Object>>() {
                });

        assertNotNull(loginResponse.getBody(),
                "Login response body should not be null. Status: " + loginResponse.getStatusCode());
        authToken = (String) loginResponse.getBody().get("token");
        assertNotNull(authToken, "Auth token should not be null. Response: " + loginResponse.getBody());
    }

    @SuppressWarnings("null")
    @Dado("que o owner tem {int} veículos cadastrados")
    public void queOOwnerTemVeiculosCadastrados(int numVehicles) {
        // Clear existing vehicles and their bookings for this owner
        List<Vehicle> existingVehicles = vehicleRepository.findByOwnerEmail(owner.getEmail());
        for (Vehicle v : existingVehicles) {
            bookingRepository.deleteAll(bookingRepository.findByVehicleId(v.getId()));
        }
        vehicleRepository.deleteAll(existingVehicles);

        // Create new vehicles
        for (int i = 1; i <= numVehicles; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setOwner(owner);
            vehicle.setBrand("Mercedes-Benz");
            vehicle.setModel("AMG GT " + i);
            vehicle.setYear(2021 + i);
            vehicle.setLicensePlate("AA-00-0" + i);
            vehicle.setSeats(2);
            vehicle.setTransmission("Automatic");
            vehicle.setFuelType("Gasoline");
            vehicle.setPricePerDay(850.0);
            vehicleRepository.save(vehicle);
        }
    }

    @Dado("que existem {int} reservas:")
    public void queExistemReservas(int numBookings, DataTable dataTable) {
        List<Vehicle> vehicles = vehicleRepository.findByOwnerEmail(owner.getEmail());
        Vehicle vehicle = vehicles.get(0); // Use first vehicle

        // Use existing renter or create a new one with unique email
        User renter = userRepository.findByEmail("test-renter@test.com")
                .orElseGet(() -> {
                    User newRenter = new User();
                    newRenter.setEmail("test-renter@test.com");
                    newRenter.setPassword("password");
                    newRenter.setRole(UserRole.RENTER);
                    return userRepository.save(newRenter);
                });

        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            Booking booking = new Booking();
            booking.setPickupDate(LocalDate.parse(row.get("pickup")));
            booking.setReturnDate(LocalDate.parse(row.get("return")));
            booking.setStatus(row.get("status"));
            booking.setTotalPrice(Double.parseDouble(row.get("valor")));
            booking.setVehicle(vehicle);
            booking.setRenter(renter);
            bookingRepository.save(booking);
        }
    }

    @Quando("acedo ao endpoint {string}")
    public void acedoAoEndpoint(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        dashboardResponse = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {
                });
    }

    @Então("devo receber status {int}")
    public void devoReceberStatus(int expectedStatus) {
        assertEquals(expectedStatus, dashboardResponse.getStatusCodeValue());
    }

    @Então("o total de ganhos deve ser {string}")
    public void oTotalDeGanhosDeveSer(String expectedRevenue) {
        Map<String, Object> body = dashboardResponse.getBody();
        assertNotNull(body);
        assertEquals(Double.parseDouble(expectedRevenue), (Double) body.get("totalRevenue"), 0.01);
    }

    @Então("o número de veículos ativos deve ser {string}")
    public void oNumeroDeVeiculosAtivosDeveSer(String expectedVehicles) {
        Map<String, Object> body = dashboardResponse.getBody();
        assertNotNull(body);
        assertEquals(Integer.parseInt(expectedVehicles), body.get("activeVehicles"));
    }

    @Então("o número de reservas pendentes deve ser {string}")
    public void oNumeroDeReservasPendentesDeveSer(String expectedPending) {
        Map<String, Object> body = dashboardResponse.getBody();
        assertNotNull(body);
        assertEquals(Integer.parseInt(expectedPending), body.get("pendingBookings"));
    }

    @Então("o número de reservas pagas deve ser {string}")
    public void oNumeroDeReservasPagasDeveSer(String expectedCompleted) {
        Map<String, Object> body = dashboardResponse.getBody();
        assertNotNull(body);
        assertEquals(Integer.parseInt(expectedCompleted), body.get("completedBookings"));
    }
}
