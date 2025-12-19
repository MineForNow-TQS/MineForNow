package tqs.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = User.builder()
                .fullName("Administrador")
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("admin123"))
                .role(UserRole.ADMIN)
                .phone("999999999") // Añade campos obligatorios
                .citizenCardNumber("00000000") // Añade campos obligatorios
                .build();
                
                repository.save(admin);
                System.out.println("✅ Usuario ADMIN creado por defecto: admin@gmail.com");
            }
        };
    }
}