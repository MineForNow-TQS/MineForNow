package tqs.backend.cucumber;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import static org.assertj.core.api.Assertions.assertThat;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BookingSteps {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @Before(value = "@SCRUM-15 or @SCRUM-16")
    public void setUp() {
        playwright = Playwright.create();
        boolean headless = false;
        String ci = System.getenv("CI");
        String display = System.getenv("DISPLAY");
        if ((ci != null && !ci.isEmpty()) || display == null || display.isEmpty()) {
            headless = true;
        }
        BrowserType.LaunchOptions opts = new BrowserType.LaunchOptions().setHeadless(headless);
        if (!headless) {
            opts.setSlowMo(500);
        }
        browser = playwright.chromium().launch(opts);
        context = browser.newContext();
        page = context.newPage();
        page.setDefaultTimeout(60000);
    }

    @After(value = "@SCRUM-15 or @SCRUM-16")
    public void tearDown() {
        if (page != null)
            page.close();
        if (context != null)
            context.close();
        if (browser != null)
            browser.close();
        if (playwright != null)
            playwright.close();
    }

    public Page getPage() {
        return page;
    }

    @Dado("que existe um veículo disponível para aluguer com ID {int}")
    public void existeVeiculoDisponivel(int id) {
        // Assumed pre-condition or managed by DB seed
    }

    @Dado("que sou um utilizador não autenticado")
    public void souUtilizadorNaoAutenticado() {
        // Context is fresh for each test due to @Before
    }

    @Dado("estou a visualizar os detalhes do veículo com ID {int}")
    public void visualizarDetalhesVeiculo(int id) {
        page.navigate("http://localhost:3000/");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pesquisar Carros")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Ver Detalhes")).first().click();
    }

    @Quando("preencho as datas de reserva {string} a {string}")
    public void preenchoDatasReserva(String startDate, String endDate) {
        page.getByRole(AriaRole.TEXTBOX).first().fill(startDate);
        page.getByRole(AriaRole.TEXTBOX).nth(1).fill(endDate);
    }

    @Quando("aciono o botão {string}")
    public void acionoOBotao(String buttonName) {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(buttonName)).click();
    }

    @Então("devo ver uma mensagem de erro {string}")
    public void devoVerMensagemErro(String message) {
        // Use Playwright assertion for visibility (waits/retries)
        assertThat(page.getByText(message)).isVisible();
    }

    @Então("devo ser redirecionado para a página de login")
    public void devoSerRedirecionadoLogin() {
        // Use Playwright assertion to wait for redirection (handling the 1500ms delay)
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/login"));
    }

    // --- Steps for the @wip scenario (placeholders to avoid
    // UndefinedStepException) ---

    @Dado("que sou um utilizador do tipo {string} autenticado")
    public void souUtilizadorAutenticado(String role) {
        // Navigate to home and register a new user
        page.navigate("http://localhost:3000/");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Criar conta")).click();

        // Fill registration form
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("João Silva")).fill("Maria Silva");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("seu@email.com")).fill("maria@email.com");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Mínimo 8 caracteres")).fill("Aa123456");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Confirme a password")).fill("Aa123456");
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Aceito os Termos de Serviço e")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Criar conta")).click();

        // Wait for registration and auto-login to complete
        page.waitForTimeout(2000);
    }

    @Quando("seleciono a data de levantamento para {string}")
    public void selecionoDataLevantamento(String date) {
        page.getByRole(AriaRole.TEXTBOX).first().fill(date);
    }

    @Quando("seleciono a data de devolução para {string}")
    public void selecionoDataDevolucao(String date) {
        page.getByRole(AriaRole.TEXTBOX).nth(1).fill(date);
    }

    @Então("devo ser redirecionado para a página de checkout")
    public void devoSerRedirecionadoCheckout() {
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/checkout.*"));
    }

    @Então("devo ver o preço total calculado para {int} dias")
    public void devoVerPrecoCalculado(int days) {
        // Verify that price calculation is visible
        // The checkout page shows "X dias x Y,00 €"
        assertThat(page.getByText(days + " dias")).isVisible();
    }

    @Então("devo ver uma mensagem de sucesso {string}")
    public void devoVerMensagemSucesso(String message) {
        assertThat(page.getByText(message)).isVisible();
    }

    @Então("devo ser redirecionado para a página de pagamento")
    public void devoSerRedirecionadoPagamento() {
        // Wait for redirect and verify URL contains /payment
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/payment.*"));
    }

    @Então("devo ver os detalhes da reserva para pagamento")
    public void devoVerDetalhesReservaPagamento() {
        // Verify payment page shows booking details
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Pagamento"))).isVisible();
        assertThat(page.getByText("Detalhes da Reserva")).isVisible();
    }
}
