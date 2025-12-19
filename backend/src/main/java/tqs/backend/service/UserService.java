package tqs.backend.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tqs.backend.dto.RegisterRequest;
import tqs.backend.dto.UpdateProfileRequest;
import tqs.backend.dto.UpgradeOwnerRequest;
import tqs.backend.dto.UserProfileResponse;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String USER_NOT_FOUND_MSG = "Utilizador não encontrado";

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
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MSG));

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
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MSG));

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
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MSG));

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

    public List<User> getAllUsers(String searchQuery) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            return userRepository.searchUsers(searchQuery);
        }
        return userRepository.findAll();
    }

    // Método para Bloquear/Desbloquear
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        user.setActive(!user.isActive()); // Inverte o estado atual
        userRepository.save(user);
    }

}
