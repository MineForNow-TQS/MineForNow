package tqs.backend.cucumber;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import tqs.backend.dto.AuthResponse;
import tqs.backend.dto.LoginRequest;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationSteps extends tqs.backend.AbstractPostgresTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private org.springframework.boot.test.web.client.TestRestTemplate restTemplate;

    private ResponseEntity<AuthResponse> response;

    @Before
    public void setUp() {
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

    @Given("a registered user with email {string} and password {string}")
    public void a_registered_user_with_email_and_password(String email, String password) {
        User user = User.builder()
                .email(email)
                .fullName("Test User")
                .passwordHash(passwordEncoder.encode(password))
                .role(UserRole.RENTER)
                .build();
        userRepository.save(user);
    }

    @When("the user logs in with email {string} and password {string}")
    public void the_user_logs_in_with_email_and_password(String email, String password) {
        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword(password);

        response = restTemplate.postForEntity("/api/auth/login", req, AuthResponse.class);
    }

    @Then("the login should be successful")
    public void the_login_should_be_successful() {
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotBlank();
    }

    @Then("the login should fail")
    public void the_login_should_fail() {
        assertThat(response.getStatusCode().value()).isIn(401, 403);
    }
}
