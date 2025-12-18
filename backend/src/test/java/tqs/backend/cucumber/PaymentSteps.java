package tqs.backend.cucumber;

import com.microsoft.playwright.*;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final CucumberSpringConfiguration config;
    private Page page;
    private User testUser;
    private Vehicle testVehicle;
    private Booking testBooking;

    public PaymentSteps(CucumberSpringConfiguration config) {
        this.config = config;
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
                    .email("renter@test.com")
                    .fullName("Maria Silva")
                    .password("password123")
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
        // TODO: Add Playwright code from recording
        // page.navigate("http://localhost:3001/payment?bookingId=" + bookingId +
        // "...");
    }

    @Quando("preencho o campo {string} com {string}")
    public void preenchoCampo(String campo, String valor) {
        // TODO: Add Playwright code from recording
        // page.getByLabel(campo).fill(valor);
    }

    @Quando("clico no botão {string}")
    public void clicoBotao(String botao) {
        // TODO: Add Playwright code from recording
        // page.getByRole(AriaRole.BUTTON, new
        // Page.GetByRoleOptions().setName(botao)).click();
    }

    @Então("devo ver a mensagem {string}")
    public void devoVerMensagem(String mensagem) {
        // TODO: Add Playwright code from recording
        // assertThat(page.getByText(mensagem)).isVisible();
    }

    @Então("a reserva deve ter o estado {string}")
    public void reservaDeveTerEstado(String estado) {
        Booking booking = bookingRepository.findById(testBooking.getId()).orElseThrow();
        assert booking.getStatus().equals(estado);
    }

    @Então("devo ver uma mensagem de erro contendo {string}")
    public void devoVerMensagemErro(String mensagem) {
        // TODO: Add Playwright code from recording
        // assertThat(page.getByText(mensagem, new
        // Page.GetByTextOptions().setExact(false))).isVisible();
    }

    @Então("devo ver mensagens de erro de validação nos campos obrigatórios")
    public void devoVerErrosValidacao() {
        // TODO: Add Playwright code from recording
        // assertThat(page.getByText("Insira os últimos 4 dígitos do
        // cartão")).isVisible();
    }
}
