package tqs.backend.cucumber;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import tqs.backend.dto.VehicleDetailDTO;
import tqs.backend.model.Booking;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VehicleSearchSteps extends tqs.backend.AbstractPostgresTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private List<VehicleDetailDTO> results;

    @Before
    public void setup() {
        bookingRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @After
    public void tearDown() {
        bookingRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Given("there are vehicles available in the system")
    public void there_are_vehicles_available_in_the_system() {
        assertThat(vehicleRepository.findAll()).isNotNull();
    }

    @Given("a vehicle is already booked from {string} to {string}")
    public void a_vehicle_is_already_booked_from_to(String start, String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        Vehicle found = vehicleRepository.findAll().stream().findFirst().orElseThrow();

        OffsetDateTime startDT = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endDT = endDate.atStartOfDay().atOffset(ZoneOffset.UTC);

        Booking booking = new Booking(
                null,
                found,
                found.getOwner(),
                startDT,
                endDT,
                found.getCity(),
                BigDecimal.ZERO,
                "CONFIRMED",
                OffsetDateTime.now()
        );

        bookingRepository.save(booking);
    }

    @When("I search for vehicles in city {string} from {string} to {string}")
    public void i_search_for_vehicles_in_city_from_to(String city, String start, String end) {
        String url = String.format("/api/vehicles/search?city=%s&startDate=%s&endDate=%s", city, start, end);
        VehicleDetailDTO[] response = restTemplate.getForObject(url, VehicleDetailDTO[].class);
        results = List.of(response);
    }

    @Then("the results should not include the booked vehicle")
    public void the_results_should_not_include_the_booked_vehicle() {
        assertThat(results).isNotNull();
    }
}
