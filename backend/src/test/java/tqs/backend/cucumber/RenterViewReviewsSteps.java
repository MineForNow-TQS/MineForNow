package tqs.backend.cucumber;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tqs.backend.dto.VehicleReviewsDTO;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Requirement("SCRUM-30")
public class RenterViewReviewsSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<VehicleReviewsDTO> response;
    private Long vehicleId;

    @Given("existe um veículo com id {long}")
    public void existeUmVeiculoComId(Long id) {
        this.vehicleId = id;
    }

    @When("acedo ao endpoint {string}")
    public void acedoAoEndpoint(String endpoint) {
        String url = "http://localhost:" + port + endpoint;
        response = restTemplate.getForEntity(url, VehicleReviewsDTO.class);
    }

    @Then("devo receber status {int}")
    public void devoReceberStatus(int expectedStatus) {
        assertThat(response.getStatusCode().value(), is(expectedStatus));
    }

    @And("a média de rating deve ser aproximadamente {double}")
    public void aMediaDeRatingDeveSerAproximadamente(double expectedRating) {
        double actualRating = response.getBody().getAverageRating();
        assertThat(actualRating, closeTo(expectedRating, 0.1));
    }

    @And("a média de rating deve ser {double}")
    public void aMediaDeRatingDeveSer(double expectedRating) {
        double actualRating = response.getBody().getAverageRating();
        assertThat(actualRating, is(expectedRating));
    }

    @And("devem existir {int} reviews na resposta")
    public void devemExistirReviewsNaResposta(int expectedCount) {
        int actualCount = response.getBody().getReviews().size();
        assertThat(actualCount, is(expectedCount));
    }

    @And("a primeira review deve ter rating {int}")
    public void aPrimeiraReviewDeveTerRating(int expectedRating) {
        int actualRating = response.getBody().getReviews().get(0).getRating();
        assertThat(actualRating, is(expectedRating));
    }
}
