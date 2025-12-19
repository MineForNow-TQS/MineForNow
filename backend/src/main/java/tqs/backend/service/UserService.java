package tqs.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tqs.backend.dto.RegisterRequest;
import tqs.backend.dto.UpdateProfileRequest;
import tqs.backend.dto.UpgradeOwnerRequest;
import tqs.backend.dto.UserProfileResponse;
import tqs.backend.model.User;
import tqs.backend.repository.UserRepository;
import tqs.backend.model.UserRole;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

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
                .citizenCardNumber(user.getCitizenCardNumber())
                .ownerMotivation(user.getOwnerMotivation())
                .role(user.getRole())
                .build();
    }

    @SuppressWarnings("null")
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
                .citizenCardNumber(savedUser.getCitizenCardNumber())
                .ownerMotivation(savedUser.getOwnerMotivation())
                .role(savedUser.getRole())
                .build();
    }

    public void requestOwnerUpgrade(String email, UpgradeOwnerRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        // Já é owner ou está pendente
        if (user.getRole() == UserRole.OWNER ||
                user.getRole() == UserRole.PENDING_OWNER) {
            throw new IllegalStateException("Pedido já submetido ou utilizador já é Owner");
        }

        // Atualizar dados legais
        user.setPhone(request.getPhone());
        user.setCitizenCardNumber(request.getCitizenCardNumber());
        user.setDrivingLicense(request.getDrivingLicense());
        user.setOwnerMotivation(request.getMotivation());

        // Estado intermédio
        user.setRole(UserRole.PENDING_OWNER);

        userRepository.save(user);
    }

    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserProfileResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .drivingLicense(user.getDrivingLicense())
                        .citizenCardNumber(user.getCitizenCardNumber())
                        .ownerMotivation(user.getOwnerMotivation())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveOwnerRequest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        if (user.getRole() != UserRole.PENDING_OWNER) {
            throw new IllegalStateException("Utilizador não tem pedido pendente");
        }

        user.setRole(UserRole.OWNER);
        userRepository.save(user);
    }

    @Transactional
    public void rejectOwnerRequest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        if (user.getRole() != UserRole.PENDING_OWNER) {
            throw new IllegalStateException("Utilizador não tem pedido pendente");
        }

        // Revert to RENTER
        user.setRole(UserRole.RENTER);
        userRepository.save(user);
    }
}
