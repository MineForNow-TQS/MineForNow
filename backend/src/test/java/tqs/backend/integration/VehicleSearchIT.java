package tqs.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import tqs.backend.model.Vehicle;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VehicleSearchIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Requirement("SCRUM-49")
    void whenSearchLisbon_thenReturnsMercedesAndFerrari() {
        // Teste real contra a BD populada pelo MinefornowApplication
        String url = "http://localhost:" + randomServerPort + "/api/vehicles/search?city=Lisboa";
        
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<List<Vehicle>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Vehicle> cars = response.getBody();
        
        // Em Lisboa temos o Mercedes e o Ferrari (dados do MinefornowApplication)
        assertThat(cars).extracting(Vehicle::getBrand)
            .contains("Mercedes-Benz", "Ferrari"); 
    }

    @Test
    @Requirement("SCRUM-49")
    void whenSearchLisbonWithConflictDates_thenMercedesIsMissing() {
        // Sabemos que o Mercedes tem reserva criada no MinefornowApplication para:
        // Hoje + 10 dias até Hoje + 15 dias.
        
        LocalDate today = LocalDate.now();
        LocalDate pickup = today.plusDays(10);
        LocalDate dropoff = today.plusDays(12);

        String url = String.format("http://localhost:%d/api/vehicles/search?city=Lisboa&pickup=%s&dropoff=%s",
                randomServerPort, pickup, dropoff);
        
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<List<Vehicle>>() {}
        );

        List<Vehicle> cars = response.getBody();

        // O Mercedes deve estar ausente porque está reservado!
        assertThat(cars).extracting(Vehicle::getBrand)
            .doesNotContain("Mercedes-Benz")
            .contains("Ferrari");
    }
}