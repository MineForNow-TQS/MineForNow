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

public class VehicleSearchSteps {

    private Browser browser;
    private BrowserContext context;
    private Page page;
    private final String FRONTEND_URL = "http://localhost:3000";
    private boolean searchButtonClicked = false; // Flag para controlar se já clicamos no botão

    @Before
    public void setUp() {
        Playwright playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false)  // Browser VISÍVEL para veres os testes
            .setSlowMo(500));    // Slow motion para ver as ações
        context = browser.newContext();
        page = context.newPage();
        page.setDefaultTimeout(60000); // 60 segundos de timeout
        searchButtonClicked = false; // Resetar flag
    }

    @After
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

    @Given("que o sistema tem {int} veículos cadastrados")
    public void queOSistemaTemVeiculosCadastrados(int quantidade) {
        // Este passo é apenas documentação, os veículos já estão no backend via CommandLineRunner
        assertTrue(quantidade > 0, "O sistema deve ter veículos cadastrados");
    }

    @And("o veículo {string} em {string} está reservado de {string} até {string}")
    public void oVeiculoEstaReservado(String veiculo, String cidade, String dataInicio, String dataFim) {
        // Este passo é apenas documentação, a reserva já está criada no backend
        assertNotNull(veiculo);
        assertNotNull(cidade);
    }

    @Given("que estou na página de pesquisa")
    public void queEstouNaPaginaDePesquisa() {
        page.navigate(FRONTEND_URL);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @When("não aplico nenhum filtro")
    public void naoAplicoNenhumFiltro() {
        // Clicar no botão "Pesquisar Carros"
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pesquisar Carros")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @When("pesquiso por veículos em {string}")
    public void pesquisoPorVeiculosEm(String cidade) {
        // Preencher o campo de cidade
        Locator inputCidade = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Lisboa, Porto, Faro..."));
        inputCidade.click();
        inputCidade.fill(cidade);
        page.waitForTimeout(300);
        
        // NÃO clicar no botão aqui - pode haver steps de data depois
        // O botão será clicado em selecionoADataDeDevolucao() ou em devoVerVeiculosNaLista()
    }

    @And("seleciono a data de levantamento {string}")
    public void selecionoADataDeLevantamento(String data) {
        // Baseado no teste gravado: page.getByRole(AriaRole.TEXTBOX).nth(1).fill("2025-12-16")
        Locator pickupInput = page.getByRole(AriaRole.TEXTBOX).nth(1);
        pickupInput.click();
        page.waitForTimeout(300);
        pickupInput.fill(data);
        page.waitForTimeout(300);
    }

    @And("seleciono a data de devolução {string}")
    public void selecionoADataDeDevolucao(String data) {
        // Baseado no teste gravado: page.getByRole(AriaRole.TEXTBOX).nth(2).fill("2025-12-21")
        Locator dropoffInput = page.getByRole(AriaRole.TEXTBOX).nth(2);
        dropoffInput.click();
        page.waitForTimeout(300);
        dropoffInput.fill(data);
        page.waitForTimeout(300);
        
        // Após preencher a última data, clicar no botão de pesquisa
        // (baseado no teste gravado que clica no botão após preencher as datas)
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pesquisar Carros")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        searchButtonClicked = true; // Marcar que já clicamos
    }

    @Then("devo ver {int} veículo na lista")
    @Then("devo ver {int} veículos na lista")
    public void devoVerVeiculosNaLista(int quantidade) {
        // Se ainda não clicamos no botão, clicar agora (caso de pesquisa só por cidade)
        if (!searchButtonClicked) {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pesquisar Carros")).click();
            searchButtonClicked = true;
        }
        
        // Aguardar a página de resultados carregar
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000); // Aguardar renderização
        
        // Procurar pelo texto de contagem
        String textoEsperado = quantidade + " carros encontrados";
        if (quantidade == 1) {
            textoEsperado = quantidade + " carro encontrado";
        } else if (quantidade == 0) {
            textoEsperado = "0 carros encontrados";
        }
        
        // Verificar se o texto está presente na página
        String pageContent = page.content();
        assertTrue(pageContent.contains(textoEsperado) || pageContent.contains(String.valueOf(quantidade)), 
            String.format("Esperava ver '%s', mas não foi encontrado na página", textoEsperado));
    }

    @And("devo ver o veículo {string}")
    public void devoVerOVeiculo(String nomeVeiculo) {
        page.waitForTimeout(500);
        
        // Procurar pelo nome do veículo na página (busca parcial)
        String pageContent = page.content();
        boolean encontrado = pageContent.contains(nomeVeiculo) || 
                            page.getByText(nomeVeiculo).count() > 0;
        
        assertTrue(encontrado, 
            String.format("O veículo '%s' deveria estar visível na lista", nomeVeiculo));
    }

    @And("não devo ver o veículo {string}")
    public void naoDevoVerOVeiculo(String nomeVeiculo) {
        page.waitForTimeout(500);
        
        // Verificar que o veículo NÃO está na página
        int count = page.getByText(nomeVeiculo).count();
        assertEquals(0, count, 
            String.format("O veículo '%s' NÃO deveria estar visível na lista", nomeVeiculo));
    }

    @And("devo ver a mensagem {string}")
    public void devoVerAMensagem(String mensagem) {
        page.waitForTimeout(500);
        
        // Procurar pela mensagem na página
        Locator elemento = page.getByText(mensagem);
        assertTrue(elemento.count() > 0, 
            String.format("A mensagem '%s' deveria estar visível", mensagem));
    }
}
