package tqs.backend.cucumber;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RenterCreateReviewSteps {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @Before
    public void setUp() {
        playwright = Playwright.create();
        // Running headless for CI/test stability unless debug needed
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        context = browser.newContext();
        page = context.newPage();
    }

    @After
    public void tearDown() {
        if (context != null)
            context.close();
        if (browser != null)
            browser.close();
        if (playwright != null)
            playwright.close();
    }

    @Dado("que estou na página do veículo com id {long}")
    public void queEstouNaPaginaDoVeiculoComId(Long id) {
        // Direct navigation as per step definition semantic
        page.navigate("http://localhost:3000/cars/" + id);
    }

    @E("que não estou logado")
    public void queNaoEstouLogado() {
        // New context implies no cookies/session
        // Additional check could be implemented if needed
    }

    @Então("não devo ver o botão de criar review")
    public void naoDevoVerOBotaoDeCriarReview() {
        Locator button = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Escrever Avaliação"));
        assertThat(button).not().isVisible();
    }
}
