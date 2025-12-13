package tqs.backend.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;

import static org.junit.jupiter.api.Assertions.*;

public class SupportSteps {

    private Browser browser;
    private BrowserContext context;
    private Page page;
    private final String frontendUrl = "http://localhost:3000";

    @Before("@SCRUM-27")
    public void setUp() {
        Playwright playwright = Playwright.create();
        boolean headless = false;
        String ci = System.getenv("CI");
        String display = System.getenv("DISPLAY");
        if ((ci != null && !ci.isEmpty()) || display == null || display.isEmpty()) {
            headless = true;
        }
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(headless ? 0 : 500));
        context = browser.newContext();
        page = context.newPage();
        page.setDefaultTimeout(60000);
    }

    @After("@SCRUM-27")
    public void tearDown() {
        if (page != null)
            page.close();
        if (context != null)
            context.close();
        if (browser != null)
            browser.close();
    }

    @Given("que estou na homepage")
    public void queEstouNaHomepage() {
        page.navigate(frontendUrl);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @When("clico no link de contactos no rodapé")
    public void clicoNoLinkDeContactos() {
        // Encontra e clica no botão "Contactos" dentro do rodapé
        // Ajustar o seletor conforme necessário para ser específico do footer se houver
        // ambiguidade
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Contactos")).click();
    }

    @Then("devo ver o modal de contactos abrir")
    public void devoVerOModalDeContactos() {
        // Verifica se o título do modal aparece
        assertTrue(page.getByText("Entre em Contacto").isVisible(),
                "O modal de contactos deve abrir e mostrar o título");
    }

    @When("preencho o formulário de contacto")
    public void preenchoOFormularioDeContacto() {
        page.fill("input[type='email']", "teste@exemplo.com");
        // Assumindo que o campo de nome tem id ou label 'Nome'
        // Se no código anterior usamos Label htmlFor="name" ... Input id="name"
        page.fill("#name", "Utilizador de Teste");
        page.fill("#subject", "Assunto de Teste");
        page.fill("#message", "Mensagem de teste para verificar a funcionalidade.");
    }

    @And("envio a mensagem")
    public void envioAMensagem() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Enviar Mensagem")).click();
    }

    @Then("devo ver uma mensagem de sucesso")
    public void devoVerUmaMensagemDeSucesso() {
        // O código anterior mostrava "Mensagem Enviada!" após o envio
        // Pode ser necessário esperar um pouco pois havia um delay simulado de 1.5s
        // Playwright auto-wait deve lidar com isso se o elemento aparecer
        // eventualmente,
        // mas podemos forçar um wait se necessário.
        assertTrue(page.getByText("Mensagem Enviada!").isVisible(),
                "A mensagem de sucesso deve aparecer após o envio");
    }

    @When("clico no link de ajuda no rodapé")
    public void clicoNoLinkDeAjuda() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Ajuda")).click();
    }

    @Then("devo ver o modal de ajuda abrir")
    public void devoVerOModalDeAjuda() {
        assertTrue(page.getByText("Centro de Ajuda").isVisible(),
                "O modal de ajuda deve ser visível");
    }

    @And("devo ver informações sobre como alugar")
    public void devoVerInformacoesSobreComoAlugar() {
        assertTrue(page.getByText("Como Alugar?").isVisible(),
                "O tópico 'Como Alugar?' deve estar presente no modal de ajuda");
    }

    @When("clico no link de termos no rodapé")
    public void clicoNoLinkDeTermos() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Termos")).click();
    }

    @Then("devo ver o modal de termos abrir")
    public void devoVerOModalDeTermos() {
        assertTrue(page.getByText("Termos e Condições").isVisible(),
                "O modal de termos deve ser visível");
    }

    @And("devo ver tópicos sobre carta de condução")
    public void devoVerTopicosSobreCartaDeConducao() {
        assertTrue(page.getByText("carta de condução válida").isVisible(),
                "Os termos devem mencionar a carta de condução");
    }
}
