package tqs.backend.service;

import java.time.OffsetDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tqs.backend.dto.RegisterRequest;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request inválido");
        }

        // Verifica confirmação de password
        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("As passwords não coincidem");
        }

        // Verifica email único
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.RENTER)
                .status("ACTIVE")
                .createdAt(OffsetDateTime.now())
                .build();

        return userRepository.save(user);
    }
}
