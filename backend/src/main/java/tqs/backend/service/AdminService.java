package tqs.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tqs.backend.dto.OwnerRequestDTO;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public List<OwnerRequestDTO> getPendingOwnerRequests() {
        return userRepository.findByRole(UserRole.PENDING_OWNER)
                .stream()
                .map(user -> OwnerRequestDTO.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .citizenCardNumber(user.getCitizenCardNumber())
                        .drivingLicense(user.getDrivingLicense())
                        .motivation(user.getMotivation())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveOwnerRequest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        if (user.getRole() != UserRole.PENDING_OWNER) {
            throw new IllegalStateException("O utilizador não tem um pedido pendente.");
        }

        user.setRole(UserRole.OWNER);
        userRepository.save(user);
    }

    @Transactional
    public void rejectOwnerRequest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        user.setRole(UserRole.RENTER); // Vuelve a ser Renter normal
        // Limpiamos los datos de la solicitud fallida si lo deseas
        user.setMotivation(null); 
        userRepository.save(user);
    }
}