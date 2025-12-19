package tqs.backend.cucumber;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.E;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
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

    @Dado("que existe um veículo com id {long}")
    public void queExisteUmVeiculoComId(Long id) {
        this.vehicleId = id;
    }

    @Quando("acedo ao endpoint de reviews {string}")
    public void acedoAoEndpointDeReviews(String endpoint) {
        String url = "http://localhost:" + port + endpoint;
        response = restTemplate.getForEntity(url, VehicleReviewsDTO.class);
    }

    @Então("devo receber status de reviews {int}")
    public void devoReceberStatusDeReviews(int expectedStatus) {
        assertThat(response.getStatusCode().value(), is(expectedStatus));
    }

    @E("a média de rating deve ser aproximadamente {double}")
    public void aMediaDeRatingDeveSerAproximadamente(double expectedRating) {
        double actualRating = response.getBody().getAverageRating();
        assertThat(actualRating, closeTo(expectedRating, 0.1));
    }

    @E("a média de rating deve ser {double}")
    public void aMediaDeRatingDeveSer(double expectedRating) {
        double actualRating = response.getBody().getAverageRating();
        assertThat(actualRating, is(expectedRating));
    }

    @E("devem existir {int} reviews na resposta")
    public void devemExistirReviewsNaResposta(int expectedCount) {
        int actualCount = response.getBody().getReviews().size();
        assertThat(actualCount, is(expectedCount));
    }

    @E("a primeira review deve ter rating {int}")
    public void aPrimeiraReviewDeveTerRating(int expectedRating) {
        int actualRating = response.getBody().getReviews().get(0).getRating();
        assertThat(actualRating, is(expectedRating));
    }
}
