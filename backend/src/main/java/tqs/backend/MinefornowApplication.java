package tqs.backend;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

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
                        tqs.backend.repository.ReviewRepository reviewRepo,
                        @Value("${minefornow.app.defaultPassword}") String defaultPassword) {
                return args -> {
                        // Check if data already exists
                        if (userRepo.findByEmail("owner@minefornow.com").isPresent()) {
                                System.out.println("Test data already exists - skipping initialization");
                                return;
                        }

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

                        // SCRUM-28: Completed booking for Mercedes review testing
                        Booking completedBooking = new Booking();
                        completedBooking.setPickupDate(LocalDate.now().minusDays(10));
                        completedBooking.setReturnDate(LocalDate.now().minusDays(5));
                        completedBooking.setStatus("COMPLETED");
                        completedBooking.setTotalPrice(850.0);
                        completedBooking.setVehicle(mercedes);
                        completedBooking.setRenter(renter);
                        bookingRepo.save(completedBooking);

                        // SCRUM-28: Completed booking for Fiat 500 review testing (Car ID 1)
                        Booking completedFiatBooking = new Booking();
                        completedFiatBooking.setPickupDate(LocalDate.now().minusDays(20));
                        completedFiatBooking.setReturnDate(LocalDate.now().minusDays(18));
                        completedFiatBooking.setStatus("COMPLETED");
                        completedFiatBooking.setTotalPrice(120.0);
                        completedFiatBooking.setVehicle(fiat);
                        completedFiatBooking.setRenter(renter);
                        bookingRepo.save(completedFiatBooking);

                        System.out.println(
                                        "E2E Test data: 5 bookings created (2 CONFIRMED, 1 WAITING_PAYMENT, 2 COMPLETED)");

                        // === SCRUM-30: Sample Reviews for All Vehicles ===

                        // Reviews for Fiat 500 (car 1)
                        tqs.backend.model.Review fiatReview1 = new tqs.backend.model.Review();
                        fiatReview1.setVehicle(fiat);
                        fiatReview1.setReviewer(renter);
                        fiatReview1.setRating(5);
                        fiatReview1.setComment("Excelente carro! Muito confortável e económico. Recomendo a todos.");
                        fiatReview1.setCreatedAt(java.time.LocalDateTime.now().minusDays(15));
                        reviewRepo.save(fiatReview1);

                        tqs.backend.model.Review fiatReview2 = new tqs.backend.model.Review();
                        fiatReview2.setVehicle(fiat);
                        fiatReview2.setReviewer(admin);
                        fiatReview2.setRating(4);
                        fiatReview2.setComment("Bom carro, mas podia ter mais espaço na bagageira.");
                        fiatReview2.setCreatedAt(java.time.LocalDateTime.now().minusDays(20));
                        reviewRepo.save(fiatReview2);

                        tqs.backend.model.Review fiatReview3 = new tqs.backend.model.Review();
                        fiatReview3.setVehicle(fiat);
                        fiatReview3.setReviewer(owner);
                        fiatReview3.setRating(5);
                        fiatReview3.setComment("Perfeito para viagens longas. O ar condicionado funciona muito bem.");
                        fiatReview3.setCreatedAt(java.time.LocalDateTime.now().minusDays(25));
                        reviewRepo.save(fiatReview3);

                        // Reviews for Nissan Juke (car 2)
                        tqs.backend.model.Review nissanReview1 = new tqs.backend.model.Review();
                        nissanReview1.setVehicle(nissan);
                        nissanReview1.setReviewer(renter);
                        nissanReview1.setRating(4);
                        nissanReview1.setComment("Carro muito bonito e confortável. A transmissão automática é ótima.");
                        nissanReview1.setCreatedAt(java.time.LocalDateTime.now().minusDays(18));
                        reviewRepo.save(nissanReview1);

                        tqs.backend.model.Review nissanReview2 = new tqs.backend.model.Review();
                        nissanReview2.setVehicle(nissan);
                        nissanReview2.setReviewer(admin);
                        nissanReview2.setRating(5);
                        nissanReview2.setComment("Adorei a experiência! Carro impecável e atendimento excelente.");
                        nissanReview2.setCreatedAt(java.time.LocalDateTime.now().minusDays(22));
                        reviewRepo.save(nissanReview2);

                        tqs.backend.model.Review nissanReview3 = new tqs.backend.model.Review();
                        nissanReview3.setVehicle(nissan);
                        nissanReview3.setReviewer(owner);
                        nissanReview3.setRating(5);
                        nissanReview3.setComment("Muito espaçoso e potente. Ideal para a família.");
                        nissanReview3.setCreatedAt(java.time.LocalDateTime.now().minusDays(27));
                        reviewRepo.save(nissanReview3);

                        // Reviews for Tesla Model 3 (car 3)
                        tqs.backend.model.Review teslaReview1 = new tqs.backend.model.Review();
                        teslaReview1.setVehicle(tesla);
                        teslaReview1.setReviewer(renter);
                        teslaReview1.setRating(4);
                        teslaReview1.setComment("Carro desportivo incrível! A aceleração é fantástica.");
                        teslaReview1.setCreatedAt(java.time.LocalDateTime.now().minusDays(16));
                        reviewRepo.save(teslaReview1);

                        tqs.backend.model.Review teslaReview2 = new tqs.backend.model.Review();
                        teslaReview2.setVehicle(tesla);
                        teslaReview2.setReviewer(admin);
                        teslaReview2.setRating(5);
                        teslaReview2.setComment("Simplesmente perfeito. Design moderno e muito conforto.");
                        teslaReview2.setCreatedAt(java.time.LocalDateTime.now().minusDays(21));
                        reviewRepo.save(teslaReview2);

                        // Reviews for Mercedes-Benz AMG GT (car 4)
                        tqs.backend.model.Review mercedesReview1 = new tqs.backend.model.Review();
                        mercedesReview1.setVehicle(mercedes);
                        mercedesReview1.setReviewer(renter);
                        mercedesReview1.setRating(5);
                        mercedesReview1.setComment("Excelente carro! Experiência incrível de condução.");
                        mercedesReview1.setCreatedAt(java.time.LocalDateTime.now().minusDays(5));
                        reviewRepo.save(mercedesReview1);

                        tqs.backend.model.Review mercedesReview2 = new tqs.backend.model.Review();
                        mercedesReview2.setVehicle(mercedes);
                        mercedesReview2.setReviewer(admin);
                        mercedesReview2.setRating(4);
                        mercedesReview2.setComment("Muito bom, mas um pouco caro para o meu orçamento.");
                        mercedesReview2.setCreatedAt(java.time.LocalDateTime.now().minusDays(2));
                        reviewRepo.save(mercedesReview2);

                        tqs.backend.model.Review mercedesReview3 = new tqs.backend.model.Review();
                        mercedesReview3.setVehicle(mercedes);
                        mercedesReview3.setReviewer(renter);
                        mercedesReview3.setRating(5);
                        mercedesReview3.setComment("Perfeito! Recomendo a todos.");
                        mercedesReview3.setCreatedAt(java.time.LocalDateTime.now().minusDays(1));
                        reviewRepo.save(mercedesReview3);

                        // Reviews for Ferrari Roma (car 5)
                        tqs.backend.model.Review ferrariReview1 = new tqs.backend.model.Review();
                        ferrariReview1.setVehicle(ferrari);
                        ferrariReview1.setReviewer(renter);
                        ferrariReview1.setRating(5);
                        ferrariReview1.setComment("SUV fantástico! Muito espaço e conforto para toda a família.");
                        ferrariReview1.setCreatedAt(java.time.LocalDateTime.now().minusDays(17));
                        reviewRepo.save(ferrariReview1);

                        tqs.backend.model.Review ferrariReview2 = new tqs.backend.model.Review();
                        ferrariReview2.setVehicle(ferrari);
                        ferrariReview2.setReviewer(admin);
                        ferrariReview2.setRating(4);
                        ferrariReview2.setComment("Bom carro para viagens. O GPS integrado é muito útil.");
                        ferrariReview2.setCreatedAt(java.time.LocalDateTime.now().minusDays(23));
                        reviewRepo.save(ferrariReview2);

                        // Reviews for Mercedes-Benz AMG GT R (car 6)
                        tqs.backend.model.Review mercedesGTRReview1 = new tqs.backend.model.Review();
                        mercedesGTRReview1.setVehicle(mercedesGTR);
                        mercedesGTRReview1.setReviewer(renter);
                        mercedesGTRReview1.setRating(5);
                        mercedesGTRReview1.setComment("Carro luxuoso e potente. Valeu cada euro!");
                        mercedesGTRReview1.setCreatedAt(java.time.LocalDateTime.now().minusDays(15));
                        reviewRepo.save(mercedesGTRReview1);

                        tqs.backend.model.Review mercedesGTRReview2 = new tqs.backend.model.Review();
                        mercedesGTRReview2.setVehicle(mercedesGTR);
                        mercedesGTRReview2.setReviewer(admin);
                        mercedesGTRReview2.setRating(5);
                        mercedesGTRReview2.setComment("Simplesmente perfeito. Design elegante e muito confortável.");
                        mercedesGTRReview2.setCreatedAt(java.time.LocalDateTime.now().minusDays(20));
                        reviewRepo.save(mercedesGTRReview2);

                        System.out.println("SCRUM-30 Test data: 14 reviews created across all vehicles");
                };
        }
}