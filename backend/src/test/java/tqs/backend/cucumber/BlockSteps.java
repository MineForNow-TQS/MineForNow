package tqs.backend.cucumber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import tqs.backend.dto.LoginRequest;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

public class BlockSteps {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final BookingSteps bookingSteps;

    public BlockSteps(BookingSteps bookingSteps) {
        this.bookingSteps = bookingSteps;
    }

    @Before(value = "@SCRUM-46", order = 0)
    public void setupScenario() {
        userRepository.deleteAll();
    }

    @Dado("que existe um utilizador comum com email {string} e password {string}")
    public void criarUtilizadorComum(String email, String password) {
        User user = User.builder()
                .email(email)
                .fullName("Utilizador de Teste")
                .password(passwordEncoder.encode(password))
                .role(UserRole.RENTER)
                .active(true)
                .build();
        userRepository.save(user);
    }

    @E("eu estou autenticado no sistema como administrador com {string} e {string}")
    public void autenticarComoAdmin(String email, String password) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User admin = User.builder()
                    .email(email)
                    .fullName("Admin Global")
                    .password(passwordEncoder.encode(password))
                    .role(UserRole.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
        }

        // CORREÇÃO: Usar construtor vazio + setters para evitar erro de compilação
        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(password);

        bookingSteps.getPage().navigate("http://localhost:3000/login");
        bookingSteps.getPage().getByPlaceholder("seu@email.com").fill(email);
        bookingSteps.getPage().getByPlaceholder("Digite a sua password").fill(password);
        
        // CORREÇÃO: Usar a opção correta da Page
        bookingSteps.getPage().getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
        
        bookingSteps.getPage().waitForURL("**/dashboard");
    }

    @Quando("eu navego para a página de gestão de utilizadores")
    public void navegarParaGestao() {
        bookingSteps.getPage().navigate("http://localhost:3000/admin/users");
    }

    @E("eu clico no botão de bloquear para o utilizador {string}")
    public void clicarBloquear(String email) {
        // CORREÇÃO: Quando usamos getByRole dentro de um locator, 
        // temos de usar Locator.GetByRoleOptions em vez de Page.GetByRoleOptions
        bookingSteps.getPage().locator("tr", new Page.LocatorOptions().setHasText(email))
                    .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Bloquear"))
                    .click();
    }

    @Então("o botão deve mudar para {string}")
    public void verificarMudancaBotao(String textoBotao) {
        // CORREÇÃO: Usar Page.GetByRoleOptions para buscas globais na página
        assertThat(bookingSteps.getPage().getByRole(AriaRole.BUTTON, 
                   new Page.GetByRoleOptions().setName(textoBotao))).isVisible();
    }

    @E("ao tentar fazer login com {string} e {string}, devo ver a mensagem {string}")
    public void validarImpedimentoLogin(String email, String password, String mensagem) {
        bookingSteps.getPage().navigate("http://localhost:3000/login");
        
        bookingSteps.getPage().getByPlaceholder("seu@email.com").fill(email);
        bookingSteps.getPage().getByPlaceholder("Digite a sua password").fill(password);
        bookingSteps.getPage().getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();

        assertThat(bookingSteps.getPage().getByText(mensagem)).isVisible();
    }
}