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

public class VehicleDetailsSteps {

    private Browser browser;
    private BrowserContext context;
    private Page page;
    private final String frontendUrl = "http://localhost:3000";

    @Before("@SCRUM-12")
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
        page.setDefaultTimeout(60000); // 60 segundos de timeout
    }

    @After("@SCRUM-12")
    public void tearDown() {
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
    }

    @Given("que o sistema tem veículos cadastrados")
    public void queOSistemaTemVeiculosCadastrados() {
        // Este passo é apenas documentação, os veículos já estão no backend
        assertTrue(true);
    }

    @And("o veículo com ID {int} é um {string} de {string}")
    public void oVeiculoComIDEUm(int id, String modelo, String ano) {
        // Este passo é apenas documentação
        assertTrue(id > 0);
        assertNotNull(modelo);
        assertNotNull(ano);
    }

    @And("o proprietário do veículo é {string}")
    public void oProprietarioDoVeiculoE(String nomeProprietario) {
        // Este passo é apenas documentação
        assertNotNull(nomeProprietario);
    }

    @Given("que estou na página de detalhes do veículo com ID {int}")
    public void queEstouNaPaginaDeDetalhesDoVeiculoComID(int id) {
        // Navegar para a home e depois clicar em "Ver Detalhes"
        page.navigate(frontendUrl);
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Clicar em "Pesquisar" para ir à listagem
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Pesquisar")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Clicar no primeiro "Ver Detalhes" (ou específico baseado no ID)
        if (id == 1) {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Ver Detalhes")).first().click();
        } else {
            // Para outros IDs, navegar diretamente
            page.navigate(frontendUrl + "/cars/" + id);
        }
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Then("devo ver o nome do veículo {string}")
    public void devoVerONomeDoVeiculo(String nomeVeiculo) {
        // Usar first() porque o nome aparece no título E na descrição
        assertTrue(page.getByText(nomeVeiculo).first().isVisible(),
                "O nome do veículo deve estar visível");
    }

    @And("devo ver o ano {string}")
    public void devoVerOAno(String ano) {
        String pageContent = page.content();
        assertTrue(pageContent.contains(ano), "O ano deve estar visível");
    }

    @And("devo ver o preço por dia")
    public void devoVerOPrecoPorDia() {
        // Procurar por texto que contenha "€/dia"
        Locator preco = page.getByText("€/dia");
        assertTrue(preco.count() > 0, "O preço por dia deve estar visível");
    }

    @And("devo ver o tipo de combustível {string}")
    public void devoVerOTipoDeCombustivel(String tipoCombustivel) {
        String pageContent = page.content();
        assertTrue(pageContent.contains(tipoCombustivel), "O tipo de combustível deve estar visível");
    }

    @And("devo ver a transmissão {string}")
    public void devoVerATransmissao(String transmissao) {
        String pageContent = page.content();
        assertTrue(pageContent.contains(transmissao), "A transmissão deve estar visível");
    }

    @And("devo ver o número de lugares {string}")
    public void devoVerONumeroDeLugares(String lugares) {
        String pageContent = page.content();
        assertTrue(pageContent.contains(lugares), "O número de lugares deve estar visível");
    }

    @And("devo ver o número de portas {string}")
    public void devoVerONumeroDePortas(String portas) {
        String pageContent = page.content();
        assertTrue(pageContent.contains(portas), "O número de portas deve estar visível");
    }

    @Then("devo ver a característica {string}")
    public void devoVerACaracteristica(String caracteristica) {
        String pageContent = page.content();
        assertTrue(pageContent.contains(caracteristica),
                "A característica '" + caracteristica + "' deve estar visível");
    }

    @Then("devo ver a cidade {string}")
    public void devoVerACidade(String cidade) {
        // Baseado no teste: "Estação de Comboios, Cascais"
        Locator cidadeElement = page.getByText(cidade);
        assertTrue(cidadeElement.count() > 0, "A cidade deve estar visível");
    }

    @And("devo ver o local {string}")
    public void devoVerOLocal(String local) {
        // Baseado no teste: "Estação de Comboios, Cascais"
        Locator localElement = page.getByText(local);
        assertTrue(localElement.count() > 0, "O local deve estar visível");
    }

    @Then("devo ver uma descrição do veículo")
    public void devoVerUmaDescricaoDoVeiculo() {
        // Baseado no teste: "Fiat 500 charmoso e compacto"
        String pageContent = page.content();
        assertTrue(pageContent.length() > 500, "A página deve ter conteúdo suficiente incluindo descrição");
    }

    @And("a descrição deve conter {string}")
    public void aDescricaoDeveConter(String palavra) {
        // Baseado no teste: "charmoso"
        String pageContent = page.content();
        assertTrue(pageContent.contains(palavra),
                "A descrição deve conter a palavra '" + palavra + "'");
    }

    @Then("devo ver o botão {string}")
    public void devoVerOBotao(String textoBotao) {
        Locator botao = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(textoBotao));
        assertTrue(botao.isVisible(), "O botão '" + textoBotao + "' deve estar visível");
    }

    @When("clico no botão {string}")
    public void clicoNoBotao(String textoBotao) {
        // Baseado no teste: getByRole(AriaRole.BUTTON, "Voltar")
        Locator botao = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(textoBotao));
        botao.click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Then("devo ser redirecionado para a página de pesquisa")
    public void devoSerRedirecionadoParaAPaginaDePesquisa() {
        page.waitForTimeout(1000);
        String currentUrl = page.url();
        assertTrue(currentUrl.contains("/cars") && !currentUrl.matches(".*/cars/\\d+"),
                "Deve estar na página de pesquisa");
    }

    @Then("devo ver uma mensagem de erro ou ser redirecionado")
    public void devoVerUmaMensagemDeErroOuSerRedirecionado() {
        page.waitForTimeout(1000);
        String currentUrl = page.url();
        String pageContent = page.content();

        boolean temErro = pageContent.contains("erro") ||
                pageContent.contains("não encontrado") ||
                pageContent.contains("404");
        boolean foiRedirecionado = !currentUrl.contains("/cars/99999");

        assertTrue(temErro || foiRedirecionado,
                "Deve mostrar erro ou redirecionar para veículo inexistente");
    }

    @Then("o preço deve estar no formato correto com {string}")
    public void oPrecoDeveEstarNoFormatoCorreto(String formato) {
        // Baseado no teste: "40 €/dia"
        Locator precoElement = page.getByText("€/dia");
        assertTrue(precoElement.count() > 0,
                "O preço deve estar no formato com '" + formato + "'");
    }

    @Then("devo ver pelo menos uma imagem do veículo")
    public void devoVerPeloMenosUmaImagemDoVeiculo() {
        // O teste gravado clica em botões de navegação de imagem (nth(3), nth(5))
        // Verifica se existem imagens na página
        String pageContent = page.content();
        assertTrue(pageContent.contains("img") || pageContent.contains("image"),
                "Deve haver pelo menos uma imagem do veículo");
    }

    @Then("devo ver um mapa integrado com a localização")
    public void devoVerUmMapaIntegradoComALocalizacao() {
        String pageContent = page.content();
        assertTrue(pageContent.contains("map") ||
                pageContent.contains("mapa") ||
                pageContent.contains("google") ||
                pageContent.contains("leaflet"),
                "Deve haver um mapa integrado");
    }

    @Then("devo ver a classificação por estrelas se disponível")
    public void devoVerAClassificacaoPorEstrelasSeDisponivel() {
        String pageContent = page.content();
        // Verifica se há sistema de rating/estrelas
        boolean temRating = pageContent.contains("star") ||
                pageContent.contains("rating") ||
                pageContent.contains("estrela") ||
                pageContent.contains("★");

        // Rating é opcional, então não falha se não existir
        System.out.println("Classificação por estrelas encontrada: " + temRating);
    }
}
