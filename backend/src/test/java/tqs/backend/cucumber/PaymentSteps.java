package tqs.backend.cucumber;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
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

import java.nio.file.Paths;
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

    private final BookingSteps bookingSteps;
    private User testUser;
    private Vehicle testVehicle;
    private Booking testBooking;

    public PaymentSteps(BookingSteps bookingSteps) {
        this.bookingSteps = bookingSteps;
    }

    @Before(value = "@SCRUM-16", order = 0)
    public void cleanDatabase() {
        // Clean database before each scenario (runs before Playwright setup)
        bookingRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SuppressWarnings("null")
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

    @SuppressWarnings("null")
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
        bookingSteps.getPage().navigate("http://localhost:3000/");
        bookingSteps.getPage().getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
        bookingSteps.getPage().getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("seu@email.com"))
                .click();
        bookingSteps.getPage().getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("seu@email.com"))
                .fill("maria@email.com");
        bookingSteps.getPage().getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Digite a sua password"))
                .click();
        bookingSteps.getPage().getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Digite a sua password"))
                .fill("Aa123456");
        bookingSteps.getPage().getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar").setExact(true))
                .click();

        // Wait for navigation to complete
        bookingSteps.getPage().waitForURL("http://localhost:3000/dashboard");

        // Navigate to payment page with booking details
        String url = String.format("http://localhost:3000/payment?bookingId=%d&carId=%d&start=%s&end=%s",
                testBooking.getId(),
                testVehicle.getId(),
                testBooking.getPickupDate().toString(),
                testBooking.getReturnDate().toString());
        bookingSteps.getPage().navigate(url);

        // Wait for page to load
        bookingSteps.getPage().waitForSelector("text=Pagamento");
    }

    @Quando("preencho o campo {string} com {string}")
    public void preenchoCampo(String campo, String valor) {
        bookingSteps.getPage().getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(campo + " *")).click();
        bookingSteps.getPage().getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(campo + " *"))
                .fill(valor);
    }

    @Então("a reserva deve ter o estado {string}")
    public void reservaDeveTerEstado(String estado) {
        // Wait a bit for the backend to update
        bookingSteps.getPage().waitForTimeout(1000);

        @SuppressWarnings("null")
        Booking booking = bookingRepository.findById(testBooking.getId()).orElseThrow();
        assert booking.getStatus().equals(estado) : "Expected status " + estado + " but got " + booking.getStatus();
    }

    @Então("devo ver uma mensagem de erro contendo {string}")
    public void devoVerMensagemErroContendo(String mensagem) {
        // Wait for error message to appear with longer timeout
        try {
            bookingSteps.getPage().waitForSelector("text=" + mensagem,
                    new Page.WaitForSelectorOptions().setTimeout(10000));
            System.out.println("✓ Mensagem de erro encontrada: " + mensagem);
        } catch (Exception e) {
            bookingSteps.getPage().screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("/tmp/payment-error-message.png")));
            System.out.println("✗ Mensagem de erro NÃO encontrada: " + mensagem);
            System.out.println("Screenshot saved to /tmp/payment-error-message.png");
            System.out.println("Current URL: " + bookingSteps.getPage().url());
            throw e;
        }

        assertThat(bookingSteps.getPage().getByText(mensagem,
                new Page.GetByTextOptions().setExact(false))).isVisible();
    }

    @Então("devo ver mensagens de erro de validação nos campos obrigatórios")
    public void devoVerErrosValidacao() {
        // Check for validation error messages
        assertThat(bookingSteps.getPage().getByText("Insira os últimos 4 dígitos do cartão")).isVisible();
    }

    @Então("devo ser redirecionado para o dashboard")
    public void devoSerRedirecionadoParaDashboard() {
        // Wait for redirect to dashboard (happens after 2 seconds)
        bookingSteps.getPage().waitForURL("**/dashboard", new Page.WaitForURLOptions().setTimeout(5000));
        System.out.println("✓ Redirecionado para dashboard: " + bookingSteps.getPage().url());
    }
}
