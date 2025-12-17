package tqs.backend.cucumber;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.E;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

import java.util.Map;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VehicleCreationSteps {

    private Browser browser;
    private BrowserContext context;
    private Page page;
    private final String frontendUrl = "http://localhost:3000";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before("@SCRUM-10")
    public void setUp() {
        Playwright playwright = Playwright.create();
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
    }

    @After("@SCRUM-10")
    public void tearDown() {
        if (page != null)
            page.close();
        if (context != null)
            context.close();
        if (browser != null)
            browser.close();
    }

    @Dado("que existe um utilizador owner registado com email {string} e password {string}")
    public void queExisteUmUtilizadorOwnerRegistadoComEmailEPassword(String email, String password) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setFullName("Owner Test");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(UserRole.OWNER);
            userRepository.save(user);
        }
    }

    @Dado("que existe um utilizador renter registado com email {string} e password {string}")
    public void queExisteUmUtilizadorRenterRegistadoComEmailEPassword(String email, String password) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setFullName("Renter Test");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(UserRole.RENTER);
            userRepository.save(user);
        }
    }

    @Dado("que estou autenticado como owner com email {string} e password {string}")
    public void queEstouAutenticadoComoOwnerComEmailEPassword(String email, String password) {
        page.navigate(frontendUrl);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("seu@email.com")).fill(email);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Digite a sua password")).fill(password);
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Lembrar-me")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar").setExact(true)).click();
        page.waitForTimeout(2000);
    }

    @Dado("que estou autenticado como renter com email {string} e password {string}")
    public void queEstouAutenticadoComoRenterComEmailEPassword(String email, String password) {
        queEstouAutenticadoComoOwnerComEmailEPassword(email, password);
    }

    @Quando("navego para a página de adicionar carro")
    public void navegoParaAPaginaDeAdicionarCarro() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Adicionar Carro")).click();
        page.waitForTimeout(1000);
    }

    @E("preencho os dados do veículo:")
    public void preenchoOsDadosDoVeiculo(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        // Fill text fields
        page.locator("input[name=\"brand\"]").fill(data.get("brand"));
        page.locator("input[name=\"model\"]").fill(data.get("model"));
        page.locator("input[name=\"year\"]").fill(data.get("year"));
        page.locator("input[name=\"mileage\"]").fill(data.get("mileage"));
        page.locator("select[name=\"type\"]").selectOption(data.get("type"));
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Ex: AA-00-BB"))
                .fill(data.get("licensePlate"));
        page.locator("select[name=\"fuel_type\"]").selectOption(data.get("fuelType"));
        page.locator("select[name=\"transmission\"]").selectOption(data.get("transmission"));
        page.locator("input[name=\"seats\"]").fill(data.get("seats"));
        page.locator("input[name=\"doors\"]").fill(data.get("doors"));

        // Checkboxes
        if ("true".equals(data.get("hasAC"))) {
            page.getByText("Ar Condicionado").click();
        }
        if ("true".equals(data.get("hasGPS"))) {
            page.getByText("GPS").click();
        }

        // Location
        page.locator("input[name=\"city\"]").fill(data.get("city"));
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Ex: Av. da Liberdade"))
                .fill(data.get("exactLocation"));

        // Price and description
        page.locator("input[name=\"price_per_day\"]").fill(data.get("pricePerDay"));
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Descreva o seu carro..."))
                .fill(data.get("description"));
    }

    @E("submeto o formulário de criação de veículo")
    public void submetoOFormularioDeCriacaoDeVeiculo() {
        // Handle dialog that may appear
        page.onceDialog(dialog -> {
            System.out.println("Dialog message: " + dialog.message());
            dialog.dismiss();
        });
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Adicionar Carro")).click();
        page.waitForTimeout(2000);
    }

    @E("clico no botão {string} sem preencher os dados")
    public void clicoNoBotaoSemPreencherOsDados(String buttonName) {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(buttonName)).click();
        page.waitForTimeout(1000);
    }

    @Então("devo ver o veículo {string} na lista de veículos")
    public void devoVerOVeiculoNaListaDeVeiculos(String vehicleName) {
        // Navigate to home or vehicle list to verify
        page.navigate(frontendUrl);
        page.waitForTimeout(2000);
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(vehicleName))).isVisible();
    }

    @Então("devo ver uma mensagem de erro de validação")
    public void devoVerUmaMensagemDeErroDeValidacao() {
        // Check for validation error - form should not submit or show error
        assertTrue(page.url().contains("add-car") || page.locator("text=obrigatório").count() > 0,
                "Deveria ver erro de validação ou permanecer na página de adicionar carro");
    }

    @Então("não devo ver o botão {string}")
    public void naoDevoVerOBotao(String buttonName) {
        page.waitForTimeout(1000);
        assertTrue(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(buttonName)).count() == 0,
                "O botão '" + buttonName + "' não deveria estar visível para um renter");
    }
}
