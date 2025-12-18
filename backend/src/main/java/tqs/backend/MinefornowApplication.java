package tqs.backend;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import tqs.backend.model.Booking;
import tqs.backend.model.User;
import java.util.Objects;
import tqs.backend.model.UserRole;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MinefornowApplication {

        private static final String DESPORTIVO = "Desportivo";
        private static final String GASOLINA = "Gasolina";
        private static final String AUTOMATICA = "Automática";

        public static void main(String[] args) {
                SpringApplication.run(MinefornowApplication.class, args);
        }

        @Bean
        @Profile("!test")
        public CommandLineRunner demo(VehicleRepository vehicleRepo, BookingRepository bookingRepo,
                        UserRepository userRepo, PasswordEncoder passwordEncoder,
                        @Value("${minefornow.app.defaultPassword}") String defaultPassword) {
                return args -> {
                        // --- CRIAR USERS ---
                        User admin = Objects.requireNonNull(User.builder()
                                        .email("admin@minefornow.com")
                                        .fullName("Admin MineForNow")
                                        .password(passwordEncoder.encode(defaultPassword))
                                        .role(UserRole.ADMIN)
                                        .phone("+351 912 345 678")
                                        .build());

                        User owner = Objects.requireNonNull(User.builder()
                                        .email("owner@minefornow.com")
                                        .fullName("João Silva")
                                        .password(passwordEncoder.encode(defaultPassword))
                                        .role(UserRole.OWNER)
                                        .phone("+351 923 456 789")
                                        .address("Rua das Flores, 123, Lisboa")
                                        .build());

                        User renter = Objects.requireNonNull(User.builder()
                                        .email("renter@minefornow.com")
                                        .fullName("Maria Santos")
                                        .password(passwordEncoder.encode(defaultPassword))
                                        .role(UserRole.RENTER)
                                        .phone("+351 934 567 890")
                                        .address("Avenida da República, 456, Porto")
                                        .build());

                        userRepo.save(admin);
                        userRepo.save(owner);
                        userRepo.save(renter);

                        // --- CARRO 1: Fiat 500 (Cascais) - Owner: admin ---
                        // Vídeo: 40€, Gasolina, Automática, 4 Lugares, 3 Portas
                        Vehicle fiat = new Vehicle(
                                        null, admin, "Fiat", "500", 2023, "Citadino", "AA-01-AA", 5000,
                                        GASOLINA, AUTOMATICA, 4, 3, true, true, true,
                                        "Cascais", "Estação de Comboios", 40.0,
                                        "Fiat 500 charmoso e compacto, ideal para passeios na cidade e estacionamento fácil.",
                                        "/Images/photo-1549317661-bd32c8ce0db2.jpeg");

                        // --- CARRO 2: Nissan Juke (Coimbra) - Owner: admin ---
                        // Vídeo: 42€, Gasolina, Manual, 5 Lugares, 5 Portas
                        Vehicle nissan = new Vehicle(
                                        null, admin, "Nissan", "Juke", 2020, "SUV", "BB-02-BB", 45000,
                                        GASOLINA, "Manual", 5, 5, true, false, true,
                                        "Coimbra", "Centro da Cidade", 42.0,
                                        "Nissan Juke, um crossover compacto e distinto, perfeito para o dia a dia e pequenas aventuras.",
                                        "/Images/photo-1609521263047-f8f205293f24.jpeg");

                        // --- CARRO 3: Tesla Model 3 (Faro) - Owner: admin ---
                        // Vídeo: 85€, Elétrico, Automática, 5 Lugares, 4 Portas
                        Vehicle tesla = new Vehicle(
                                        null, admin, "Tesla", "Model 3", 2023, "Sedan", "CC-03-CC", 10000,
                                        "Elétrico", AUTOMATICA, 5, 4, true, true, true,
                                        "Faro", "Aeroporto de Faro", 85.0,
                                        "Tesla Model 3 elétrico, tecnologia de ponta e sustentável.",
                                        "/Images/photo-1560958089-b8a1929cea89.jpeg");

                        // --- CARRO 4: Mercedes-Benz AMG GT (Lisboa) - Owner: owner ---
                        // Vídeo: 850€, Gasolina, Automática, 2 Lugares, 2 Portas
                        Vehicle mercedes = new Vehicle(
                                        null, owner, "Mercedes-Benz", "AMG GT", 2021, DESPORTIVO, "DD-04-DD", 18000,
                                        GASOLINA, AUTOMATICA, 2, 2, true, true, true,
                                        "Lisboa", "Avenida da Liberdade", 850.0,
                                        "Mercedes-AMG GT de luxo. Design deslumbrante e performance inigualável. Perfeito para uma experiência exclusiva.",
                                        "/Images/photo-1617814076367-b759c7d7e738.jpeg");

                        // --- CARRO 5: Ferrari Roma (Lisboa) - Owner: owner ---
                        // Vídeo: 950€, Gasolina, Automática, 4 Lugares (2+2), 2 Portas
                        Vehicle ferrari = new Vehicle(
                                        null, owner, "Ferrari", "Roma", 2024, DESPORTIVO, "EE-05-EE", 1000,
                                        GASOLINA, AUTOMATICA, 2, 2, true, true, true,
                                        "Lisboa", "Parque das Nações", 950.0,
                                        "Ferrari Roma desportivo de luxo, uma experiência de condução inesquecível.",
                                        "/Images/photo-1606220838315-056192d5e927.jpeg");

                        // --- CARRO 6: Mercedes-Benz AMG GT R (Porto) - Owner: owner ---
                        // Vídeo: 1100€, Gasolina, Automática, 2 Lugares, 2 Portas
                        Vehicle mercedesGTR = new Vehicle(
                                        null, owner, "Mercedes-Benz", "AMG GT R", 2022, DESPORTIVO, "FF-06-FF", 10000,
                                        GASOLINA, AUTOMATICA, 2, 2, true, true, true,
                                        "Porto", "Foz do Douro", 1100.0,
                                        "Mercedes-AMG GT R, a máquina de performance definitiva. Edição especial com detalhes amarelos.",
                                        "/Images/photo-1618843479313-40f8afb4b4d8.jpeg");

                        // Salvar Veículos
                        vehicleRepo.save(fiat);
                        vehicleRepo.save(nissan);
                        vehicleRepo.save(tesla);
                        vehicleRepo.save(mercedes);
                        vehicleRepo.save(ferrari);
                        vehicleRepo.save(mercedesGTR);

                        // Reserva relativa a hoje para tornar os testes independentes da data atual
                        LocalDate today = LocalDate.now();
                        LocalDate pickup = today.plusDays(10); // Daqui a 10 dias
                        LocalDate dropoff = today.plusDays(15); // Daqui a 15 dias

                        bookingRepo.save(new Booking(null, pickup, dropoff, mercedes));

                        // === E2E Test Bookings ===
                        // Create 3 bookings for owner dashboard E2E tests
                        Booking testBooking1 = new Booking();
                        testBooking1.setPickupDate(LocalDate.of(2025, 12, 22));
                        testBooking1.setReturnDate(LocalDate.of(2025, 12, 25));
                        testBooking1.setStatus("CONFIRMED");
                        testBooking1.setTotalPrice(850.0);
                        testBooking1.setVehicle(mercedes);
                        testBooking1.setRenter(renter);
                        bookingRepo.save(testBooking1);

                        Booking testBooking2 = new Booking();
                        testBooking2.setPickupDate(LocalDate.of(2025, 12, 26));
                        testBooking2.setReturnDate(LocalDate.of(2025, 12, 27));
                        testBooking2.setStatus("CONFIRMED");
                        testBooking2.setTotalPrice(1700.0);
                        testBooking2.setVehicle(mercedes);
                        testBooking2.setRenter(renter);
                        bookingRepo.save(testBooking2);

                        Booking testBooking3 = new Booking();
                        testBooking3.setPickupDate(LocalDate.of(2025, 12, 28));
                        testBooking3.setReturnDate(LocalDate.of(2025, 12, 30));
                        testBooking3.setStatus("WAITING_PAYMENT");
                        testBooking3.setTotalPrice(1100.0);
                        testBooking3.setVehicle(mercedes);
                        testBooking3.setRenter(renter);
                        bookingRepo.save(testBooking3);

                        System.out.println("✅ E2E Test data: 3 bookings created (2 CONFIRMED, 1 WAITING_PAYMENT)");
                };
        }
}```