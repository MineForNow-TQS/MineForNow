package tqs.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tqs.backend.dto.RegisterRequest;
import tqs.backend.model.User;
import tqs.backend.repository.UserRepository;
import tqs.backend.model.UserRole;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @SuppressWarnings("null")
    public User register(RegisterRequest request) {

        // Verifica confirmação de password
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("As passwords não coincidem");
        }

        // Verifica email único
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        // Cria User com password criptografada
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.RENTER)
                .build();

        return userRepository.save(user);
    }
}
