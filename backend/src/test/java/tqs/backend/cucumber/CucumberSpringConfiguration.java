package tqs.backend.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tqs.backend.MinefornowApplication;
import tqs.backend.testsupport.AbstractPostgresTest;

@CucumberContextConfiguration
@SpringBootTest(classes = MinefornowApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfiguration extends AbstractPostgresTest {
}
