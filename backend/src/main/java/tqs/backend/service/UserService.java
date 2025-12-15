package tqs.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tqs.backend.dto.RegisterRequest;
import tqs.backend.dto.UpdateProfileRequest;
import tqs.backend.dto.UserProfileResponse;
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

    public UserProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .drivingLicense(user.getDrivingLicense())
                .role(user.getRole())
                .build();
    }

    public UserProfileResponse updateUserProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getDrivingLicense() != null) {
            user.setDrivingLicense(request.getDrivingLicense());
        }

        User savedUser = userRepository.save(user);

        return UserProfileResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .drivingLicense(savedUser.getDrivingLicense())
                .role(savedUser.getRole())
                .build();
    }
}
