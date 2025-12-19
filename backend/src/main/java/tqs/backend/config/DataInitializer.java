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
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@gmail.com";

            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = User.builder()
                        .fullName("Admin Principal")
                        .email(adminEmail)
                        // Define a password como "admin123"
                        .password(passwordEncoder.encode("admin123"))
                        .role(UserRole.ADMIN)
                        .active(true)
                        .phone("910000000")
                        .build();

                userRepository.save(admin);
                System.out.println("✅ USUÁRIO ADMIN CONFIGURADO: " + adminEmail + " / admin123");
            }
        };
    }
}