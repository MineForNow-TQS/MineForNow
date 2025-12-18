package tqs.backend.cucumber;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import tqs.backend.model.Booking;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PaymentSteps {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final CucumberSpringConfiguration config;
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private User testUser;
    private Vehicle testVehicle;
    private Booking testBooking;

    public PaymentSteps(CucumberSpringConfiguration config) {
        this.config = config;
    }

    @Before("@SCRUM-16")
    public void setUp() {
        // Clean database before each scenario
        bookingRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        // Initialize Playwright
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

    @After("@SCRUM-16")
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

    @Dado("que existe um veículo disponível com ID {int}")
    public void existeVeiculoDisponivel(int vehicleId) {
        // Create owner
        User owner = User.builder()
                .email("owner@test.com")
                .fullName("Test Owner")
                .password("password123")
                .role(UserRole.OWNER)
                .build();
        owner = userRepository.save(owner);

        // Create vehicle
        testVehicle = new Vehicle();
        testVehicle.setOwner(owner);
        testVehicle.setBrand("Tesla");
        testVehicle.setModel("Model 3");
        testVehicle.setYear(2023);
        testVehicle.setLicensePlate("AB-12-CD");
        testVehicle.setPricePerDay(100.0);
        testVehicle.setCity("Lisboa");
        testVehicle = vehicleRepository.save(testVehicle);
    }

    @Dado("que existe uma reserva com ID {int} no estado {string}")
    public void existeReservaComEstado(int bookingId, String status) {
        // Create renter if not exists
        if (testUser == null) {
            testUser = User.builder()
                    .email("maria@email.com")
                    .fullName("Maria Silva")
                    .password(passwordEncoder.encode("Aa123456"))
                    .role(UserRole.RENTER)
                    .build();
            testUser = userRepository.save(testUser);
        }

        // Create booking
        testBooking = new Booking(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                testVehicle,
                testUser,
                status,
                400.0);
        testBooking = bookingRepository.save(testBooking);
    }

    @Dado("estou na página de pagamento da reserva com ID {int}")
    public void estouNaPaginaPagamento(int bookingId) {
        // Navigate and login
        page.navigate("http://localhost:3000/");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("seu@email.com")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("seu@email.com")).fill("maria@email.com");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Digite a sua password")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Digite a sua password")).fill("Aa123456");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar").setExact(true)).click();

        // Wait for navigation to complete
        page.waitForURL("http://localhost:3000/dashboard");

        // Navigate to payment page with booking details
        String url = String.format("http://localhost:3000/payment?bookingId=%d&carId=%d&start=%s&end=%s",
                testBooking.getId(),
                testVehicle.getId(),
                testBooking.getPickupDate().toString(),
                testBooking.getReturnDate().toString());
        page.navigate(url);

        // Wait for page to load
        page.waitForSelector("text=Pagamento");
    }

    @Quando("preencho o campo {string} com {string}")
    public void preenchoCampo(String campo, String valor) {
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(campo + " *")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(campo + " *")).fill(valor);
    }

    @Então("a reserva deve ter o estado {string}")
    public void reservaDeveTerEstado(String estado) {
        // Wait a bit for the backend to update
        page.waitForTimeout(1000);

        Booking booking = bookingRepository.findById(testBooking.getId()).orElseThrow();
        assert booking.getStatus().equals(estado) : "Expected status " + estado + " but got " + booking.getStatus();
    }

    @Então("devo ver uma mensagem de erro contendo {string}")
    public void devoVerMensagemErroContendo(String mensagem) {
        // Wait for error message to appear
        page.waitForSelector("text=" + mensagem, new Page.WaitForSelectorOptions().setTimeout(5000));
        assertThat(page.getByText(mensagem, new Page.GetByTextOptions().setExact(false))).isVisible();
    }

    @Então("devo ver mensagens de erro de validação nos campos obrigatórios")
    public void devoVerErrosValidacao() {
        // Check for validation error messages
        assertThat(page.getByText("Insira os últimos 4 dígitos do cartão")).isVisible();
    }
}
