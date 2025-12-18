package tqs.backend.cucumber;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

public class BecomeOwnerSteps {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Playwright playwright;
    private Browser browser;
    private Page page;

    private User testUser;

    // ---------- SETUP & TEARDOWN ----------

    @Before(value = "@SCRUM-65", order = 0)
    public void setup() {
        userRepository.deleteAll();

        playwright = Playwright.create();
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(500)
        );
        page = browser.newPage();
    }

    @After(value = "@SCRUM-65")
    public void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    // ---------- GIVEN ----------

    @Dado("que eu estou autenticado como um utilizador comum")
    public void autenticadoComoUtilizadorComum() {
        testUser = User.builder()
                .email("test_renter@email.com")
                .fullName("João Renter")
                .password(passwordEncoder.encode("Aa123456"))
                .role(UserRole.RENTER)
                .build();

        userRepository.save(testUser);

        page.navigate("http://localhost:3000/login");
        page.getByPlaceholder("seu@email.com").fill(testUser.getEmail());
        page.getByPlaceholder("Digite a sua password").fill("Aa123456");
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Entrar")).click();

        page.waitForURL("**/dashboard");
    }

    @Dado("que eu já submeti uma candidatura anteriormente")
    public void jaSubmetiCandidatura() {
        testUser.setRole(UserRole.PENDING_OWNER);
        userRepository.save(testUser);
        page.reload();
    }

    @Dado("que eu acedo à página {string}")
    public void acedoPagina(String pagina) {
        page.navigate("http://localhost:3000/become-owner");
        page.waitForLoadState();
    }

    // ---------- WHEN ----------

    @Quando("eu preencho o formulário de candidatura")
    public void preenchoFormulario() {
        page.getByPlaceholder("912 345 678").fill("912345678");
        page.getByPlaceholder("12345678").fill("12345678");
        page.getByPlaceholder("Duas letras e seis números").fill("PT123456");
        page.getByPlaceholder("Conte-nos brevemente a sua motivação...")
            .fill("Quero rentabilizar o meu carro.");
    }

    @Quando("eu clico no botão {string}")
    public void clicoNoBotao(String nomeBotao) {
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(nomeBotao)).click();
    }

    // ---------- THEN ----------

    @Então("eu devo ver a mensagem {string}")
    public void devoVerMensagem(String mensagem) {
        assertThat(page.getByText(mensagem)).isVisible();
    }

    @Então("o meu estado na base de dados deve ser {string}")
    public void estadoNaBaseDeDadosDeveSer(String roleEsperado) {
        User updatedUser = userRepository
                .findByEmail(testUser.getEmail())
                .orElseThrow();

        assertEquals(UserRole.valueOf(roleEsperado), updatedUser.getRole());
    }

    @Então("eu não devo ver o formulário de candidatura")
    public void naoDevoVerFormulario() {
        assertThat(page.getByPlaceholder("912 345 678")).isHidden();
    }
}
