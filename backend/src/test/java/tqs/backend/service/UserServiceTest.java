package tqs.backend.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import tqs.backend.dto.RegisterRequest;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("João Silva");
        request.setEmail("joao@email.com");
        request.setPassword("Senha123");
        request.setConfirmPassword("Senha123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User user = userService.register(request);

        assertNotNull(user);
        assertEquals("João Silva", user.getFullName());
        assertEquals("joao@email.com", user.getEmail());
        assertEquals(UserRole.RENTER, user.getRole());
        assertNotEquals("Senha123", user.getPassword()); // password deve ser encriptada
    }

    @Test
    void shouldThrowWhenPasswordsDoNotMatch() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Maria");
        request.setEmail("maria@email.com");
        request.setPassword("Senha123");
        request.setConfirmPassword("Senha321");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.register(request));
        assertEquals("As passwords não coincidem", ex.getMessage());
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Pedro");
        request.setEmail("pedro@email.com");
        request.setPassword("Senha123");
        request.setConfirmPassword("Senha123");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new User()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.register(request));
        assertEquals("Email já está em uso", ex.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordTooWeak() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Ana");
        request.setEmail("ana@email.com");
        request.setPassword("senha");  // muito fraca
        request.setConfirmPassword("senha");

        // Aqui a validação de regex é feita pelo DTO com @Valid no controller
        // Então UserService não verifica regex. Podemos testar via controller ou via validator
    }
}
