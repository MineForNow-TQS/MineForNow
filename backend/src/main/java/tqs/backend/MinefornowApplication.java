package tqs.backend;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import tqs.backend.model.Booking;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.VehicleRepository;

@SpringBootApplication
public class MinefornowApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinefornowApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner demo(VehicleRepository vehicleRepo, BookingRepository bookingRepo) {
        return (args) -> {
            // --- CARRO 1: Fiat 500 (Cascais) ---
            // Vídeo: 40€, Gasolina, Automática, 4 Lugares, 3 Portas
            Vehicle fiat = new Vehicle(
                null, "Fiat", "500", 2021, "Citadino", "AA-01-AA", 15000, 
                "Gasolina", "Automática", 4, 3, true, true, true, 
                "Cascais", "Estação de Comboios", 40.0, 
                "Fiat 500 charmoso e compacto, ideal para passeios na cidade e estacionamento fácil.", 
                "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2" // Azul claro
            );

            // --- CARRO 2: Nissan Juke (Coimbra) ---
            // Vídeo: 42€, Gasolina, Manual, 5 Lugares, 5 Portas
            Vehicle nissan = new Vehicle(
                null, "Nissan", "Juke", 2020, "SUV", "BB-02-BB", 42000, 
                "Gasolina", "Manual", 5, 5, true, true, true, 
                "Coimbra", "Centro da Cidade", 42.0, 
                "SUV compacto com design arrojado, perfeito para viagens confortáveis.", 
                "https://images.unsplash.com/photo-1567818735868-e71b99932e29" // Exemplo SUV
            );

            // --- CARRO 3: Tesla Model 3 (Faro) ---
            // Vídeo: 85€, Elétrico, Automática, 5 Lugares, 4 Portas
            Vehicle tesla = new Vehicle(
                null, "Tesla", "Model 3", 2023, "Sedan", "CC-03-CC", 5000, 
                "Elétrico", "Automática", 5, 4, true, true, true, 
                "Faro", "Aeroporto de Faro", 85.0, 
                "Tecnologia de ponta e condução silenciosa. Autonomia excelente para o Algarve.", 
                "https://images.unsplash.com/photo-1560958089-b8a1929cea89" // Branco
            );

            // --- CARRO 4: Mercedes-Benz AMG GT (Lisboa) ---
            // Vídeo: 850€, Gasolina, Automática, 2 Lugares, 2 Portas
            Vehicle mercedes = new Vehicle(
                null, "Mercedes-Benz", "AMG GT", 2021, "Desportivo", "DD-04-DD", 8000, 
                "Gasolina", "Automática", 2, 2, true, true, true, 
                "Lisboa", "Avenida da Liberdade", 850.0, 
                "Performance pura e luxo inigualável. Uma experiência de condução inesquecível.", 
                "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8" // Cinzento
            );

            // --- CARRO 5: Ferrari Roma (Lisboa) ---
            // Vídeo: 950€, Gasolina, Automática, 4 Lugares (2+2), 2 Portas
            Vehicle ferrari = new Vehicle(
                null, "Ferrari", "Roma", 2024, "Desportivo", "EE-05-EE", 1200, 
                "Gasolina", "Automática", 4, 2, true, true, true, 
                "Lisboa", "Parque das Nações", 950.0, 
                "Elegância intemporal e potência Ferrari. O Grand Tourer definitivo.", 
                "https://images.unsplash.com/photo-1592198084033-aade902d1aae" // Vermelho
            );

            // --- CARRO 6: Mercedes-Benz AMG GT R (Porto) ---
            // Vídeo: 1100€, Gasolina, Automática, 2 Lugares, 2 Portas
            Vehicle mercedesGTR = new Vehicle(
                null, "Mercedes-Benz", "AMG GT R", 2022, "Desportivo", "FF-06-FF", 2500, 
                "Gasolina", "Automática", 2, 2, true, true, true, 
                "Porto", "Foz do Douro", 1100.0, 
                "A besta do Inferno Verde. Performance de pista para a estrada.", 
                "https://images.unsplash.com/photo-1617788138017-80ad40651399" // Cinzento escuro
            );

            // Salvar Veículos
            vehicleRepo.save(fiat);
            vehicleRepo.save(nissan);
            vehicleRepo.save(tesla);
            vehicleRepo.save(mercedes);
            vehicleRepo.save(ferrari);
            vehicleRepo.save(mercedesGTR);

            System.out.println("--- 6 VEÍCULOS CARREGADOS ---");

            // --- DADOS PARA TESTAR A US SCRUM-49 ---
            // Vamos criar uma reserva para o MERCEDES AMG GT (Lisboa)
            // Para testar: Se pesquisar em Lisboa nessas datas, o Mercedes NÃO deve aparecer.
            
            LocalDate today = LocalDate.now();
            LocalDate pickup = today.plusDays(10); // Daqui a 10 dias
            LocalDate dropoff = today.plusDays(15); // Daqui a 15 dias

            bookingRepo.save(new Booking(null, pickup, dropoff, mercedes));

            System.out.println("--- RESERVA DE TESTE CRIADA ---");
            System.out.println("Veículo: Mercedes AMG GT (Lisboa)");
            System.out.println("Ocupado de: " + pickup + " até " + dropoff);
            System.out.println("Teste a pesquisa nessas datas para verificar se ele é excluído.");
        };
    }
}