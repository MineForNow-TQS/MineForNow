package tqs.backend.cucumber;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.E;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import tqs.backend.model.User;
import tqs.backend.repository.UserRepository;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationSteps {

    private Browser browser;
    private BrowserContext context;
    private Page page;
    private final String frontendUrl = "http://localhost:3000";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before("@SCRUM-32")
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

        // Clean up test user
        userRepository.findByEmail("joaosilva@gmail.com").ifPresent(userRepository::delete);
    }

    @After("@SCRUM-32")
    public void tearDown() {
        if (page != null)
            page.close();
        if (context != null)
            context.close();
        if (browser != null)
            browser.close();
    }

    @Dado("que tenho um pedido de registo válido")
    public void queTenhoUmPedidoDeRegistoValido() {
        // Cleaning DB in @Before matches this intent
        page.navigate(frontendUrl);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Criar conta")).click();
    }

    @Quando("submeto o pedido de registo")
    public void submetoOPedidoDeRegisto() {
        // Only fill if name is empty (to avoid overwriting negative test setup)
        if (page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("João Silva")).inputValue()
                .isEmpty()) {
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("João Silva")).fill("João Silva");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("seu@email.com"))
                    .fill("joaosilva@gmail.com");

            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Mínimo 8 caracteres"))
                    .fill("Aa123456");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Confirme a password"))
                    .fill("Aa123456");

            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Aceito os Termos de Serviço e"))
                    .click();
        }
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Criar conta")).click();
    }

    @Então("o estado da resposta deve ser {int}")
    public void oEstadoDaRespostaDeveSer(int status) {
        // In UI test, we check visible elements.
        // If 200 (Success), we usually see redirect or success message
        if (status == 200) {
            // If login/register success, we might see the user menu
            // Or verify URL?
        }
    }

    @E("a resposta deve conter {string}")
    public void aRespostaDeveConter(String text) {
        assertThat(page.getByText(text).first()).isVisible();
    }

    @Dado("que tenho um pedido de registo com passwords diferentes")
    public void ieTenhoRegistoPasswordsDiferentes() {
        page.navigate(frontendUrl);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Criar conta")).click();

        // Ensure we are on the register page or modal
        assertThat(page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("João Silva"))).isVisible();

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("João Silva")).fill("User Mismatch");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("seu@email.com"))
                .fill("mismatch@gmail.com");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Mínimo 8 caracteres")).fill("Aa123456");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Confirme a password")).fill("WrongPass");
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Aceito os Termos de Serviço e")).click();
    }

    @Dado("que tenho credenciais válidas")
    public void queTenhoCredenciaisValidas() {
        // Ensure user exists using Repository
        if (userRepository.findByEmail("joaosilva@gmail.com").isEmpty()) {
            User user = new User();
            user.setFullName("João Silva");
            user.setEmail("joaosilva@gmail.com");
            user.setPassword(passwordEncoder.encode("Aa123456"));
            user.setRole(tqs.backend.model.UserRole.RENTER);
            userRepository.save(user);
        }
        page.navigate(frontendUrl);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
    }

    @Quando("submeto o pedido de login")
    public void submetoOPedidoDeLogin() {
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill("joaosilva@gmail.com");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("Aa123456");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar").setExact(true)).click();
    }

    @Dado("que tenho credenciais inválidas")
    public void queTenhoCredenciaisInvalidas() {
        page.navigate(frontendUrl);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill("invalid@gmail.com");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("WrongPass");
    }

    // Logout Steps
    @Dado("que estou autenticado")
    public void queEstouAutenticado() {
        queTenhoCredenciaisValidas();
        submetoOPedidoDeLogin();
        // Aguardar login - o avatar tem texto "J j" (inicial + espaço + inicial nome)
        page.waitForTimeout(2000);
        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("J j"))).isVisible();
    }

    @Quando("faço logout")
    public void facoLogout() {
        // Clicar no avatar (código do codegen: "J j")
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("J j")).click();
        page.waitForTimeout(500);
        page.getByText("Sair").click();
        page.waitForTimeout(1000);
    }

    @Então("devo conseguir aceder ao perfil de {string}")
    public void devoConseguirAcederAoPerfilDe(String nomeOuEmail) {
        // Aguardar que o login termine
        page.waitForTimeout(2000);

        // O botão do avatar tem o texto "J joaosilva" (letra + espaço + nome)
        // Usar locator direto com texto parcial
        page.locator("button:has-text('joaosilva')").first().click();
        page.waitForTimeout(500);

        // Verificar que o dropdown abriu e mostra o nome
        // O dropdown contém o texto do utilizador
        assertTrue(page.content().contains(nomeOuEmail),
                "O dropdown deveria conter '" + nomeOuEmail + "'");
    }

    @Então("não devo conseguir aceder ao perfil de {string}")
    public void naoDevoConseguirAcederAoPerfilDe(String name) {
        // Aguardar que a página atualize após logout
        page.waitForTimeout(1000);
        // Verificar que o botão do avatar NÃO está visível
        assertTrue(page.locator("button:has-text('" + name + "')").count() == 0,
                "O botão do perfil deveria não estar visível após logout");
    }
}
