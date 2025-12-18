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
import org.springframework.http.ResponseEntity;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
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
    private ResponseEntity<Map> dashboardResponse;

    @Dado("que existe um owner autenticado com email {string}")
    public void queExisteUmOwnerAutenticadoComEmail(String email) {
        // Create owner user
        owner = new User();
        owner.setEmail(email);
        owner.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"); // password123
        owner.setRole("OWNER");
        owner = userRepository.save(owner);

        // Login to get token
        Map<String, String> loginRequest = Map.of(
                "email", email,
                "password", "password123");

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                "/api/auth/login",
                loginRequest,
                Map.class);

        authToken = (String) loginResponse.getBody().get("token");
        assertNotNull(authToken, "Auth token should not be null");
    }

    @Dado("que o owner tem {int} veículos cadastrados")
    public void queOOwnerTemVeiculosCadastrados(int numVehicles) {
        for (int i = 1; i <= numVehicles; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setOwner(owner);
            vehicle.setBrand("Mercedes-Benz");
            vehicle.setModel("AMG GT " + i);
            vehicle.setYear(2021 + i);
            vehicle.setLicensePlate("AA-00-0" + i);
            vehicle.setColor("Black");
            vehicle.setSeats(2);
            vehicle.setTransmission("Automatic");
            vehicle.setFuelType("Gasoline");
            vehicle.setPricePerDay(850.0);
            vehicle.setLocation("Lisboa");
            vehicle.setAvailable(true);
            vehicleRepository.save(vehicle);
        }
    }

    @Dado("que existem {int} reservas:")
    public void queExistemReservas(int numBookings, DataTable dataTable) {
        List<Vehicle> vehicles = vehicleRepository.findByOwnerEmail(owner.getEmail());
        Vehicle vehicle = vehicles.get(0); // Use first vehicle

        // Create a renter user
        User renter = new User();
        renter.setEmail("renter@test.com");
        renter.setPassword("password");
        renter.setRole("RENTER");
        renter = userRepository.save(renter);

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
                Map.class);
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
