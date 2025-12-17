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
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class UserProfileSteps {

    private Browser browser;
    private BrowserContext context;
    private Page page;
    private final String frontendUrl = "http://localhost:3000";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before("@SCRUM-46")
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
        page.setDefaultTimeout(60000);

        // Clean up test users
        userRepository.findByEmail("joao@gmail.com").ifPresent(userRepository::delete);
    }

    @After("@SCRUM-46")
    public void tearDown() {
        if (page != null)
            page.close();
        if (context != null)
            context.close();
        if (browser != null)
            browser.close();
    }

    // ===================== DADO =====================

    @Dado("que não tenho conta no sistema")
    public void queNaoTenhoContaNoSistema() {
        // Já limpamos no @Before
        userRepository.findByEmail("joao@gmail.com").ifPresent(userRepository::delete);
    }

    @Dado("que existe um utilizador {string} no sistema")
    public void queExisteUmUtilizadorNoSistema(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setFullName("Joao");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("Aa123456"));
            user.setRole(UserRole.RENTER);
            userRepository.save(user);
        }
    }

    @Dado("que estou autenticado como {string}")
    public void queEstouAutenticadoComo(String email) {
        queExisteUmUtilizadorNoSistema(email);
        facoLoginComE(email, "Aa123456");
    }

    @E("estou na página de definições")
    public void estouNaPaginaDeDefinicoes() {
        acedoAoPainelDeDefinicoes();
    }

    // ===================== QUANDO =====================

    @Quando("me registo com o nome {string} e email {string}")
    public void meRegistoComONomeEEmail(String nome, String email) {
        page.navigate(frontendUrl);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Criar conta")).click();

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("João Silva")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("João Silva")).fill(nome);

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("seu@email.com")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("seu@email.com")).fill(email);

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Mínimo 8 caracteres")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Mínimo 8 caracteres")).fill("Aa123456");

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Confirme a password")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Confirme a password")).fill("Aa123456");

        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Aceito os Termos de Serviço e")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Criar conta")).click();

        // Esperar pelo login automático
        page.waitForTimeout(2000);
    }

    @Quando("faço login com {string} e {string}")
    public void facoLoginComE(String email, String password) {
        page.navigate(frontendUrl);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill(email);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill(password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar").setExact(true)).click();

        page.waitForTimeout(2000);
    }

    @E("acedo ao painel de definições")
    public void acedoAoPainelDeDefinicoes() {
        page.waitForTimeout(1000);

        // O botão do avatar tem texto "J joao" - usar texto parcial
        page.locator("button:has-text('joao')").first().click();
        page.waitForTimeout(500);

        // Clicar em "Painel" no dropdown
        page.getByText("Painel").click();
        page.waitForTimeout(1000);

        // Clicar no botão "Definições" na sidebar do dashboard
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Definições")).click();
        page.waitForTimeout(1000);
    }

    @E("preencho o telefone com {string}")
    public void preenchoOTelefoneCom(String telefone) {
        Locator phoneInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("+351 912 345"));
        phoneInput.click();
        phoneInput.fill(telefone);
    }

    @E("preencho a carta de condução com {string}")
    public void preenchoACartaDeConducaoCom(String cartaConducao) {
        Locator licenseInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("AB123456"));
        licenseInput.click();
        licenseInput.fill(cartaConducao);
    }

    @E("clico em {string}")
    public void clicoEm(String textoBotao) {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(textoBotao)).click();
        page.waitForTimeout(1500);
    }

    // ===================== ENTÃO =====================

    @Então("devo ver a mensagem de sucesso {string}")
    public void devoVerAMensagemDeSucesso(String mensagem) {
        page.waitForTimeout(500);
        Locator msgLocator = page.getByText(mensagem);
        assertThat(msgLocator.first()).isVisible();
    }

    @Então("devo ver o título {string}")
    public void devoVerOTitulo(String titulo) {
        assertThat(page.getByText(titulo).first()).isVisible();
    }

    @E("devo ver os campos de telefone e carta de condução")
    public void devoVerOsCamposDeTelefoneECartaDeConducao() {
        assertThat(page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("+351 912 345"))).isVisible();
        assertThat(page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("AB123456"))).isVisible();
    }

    @Então("devo ver o texto {string}")
    public void devoVerOTexto(String texto) {
        assertThat(page.getByText(texto).first()).isVisible();
    }

    @E("devo ver o botão de sair {string}")
    public void devoVerOBotaoDeSair(String textoBotao) {
        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(textoBotao))).isVisible();
    }
}
