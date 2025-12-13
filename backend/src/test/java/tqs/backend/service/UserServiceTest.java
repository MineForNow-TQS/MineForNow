package tqs.backend.service;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

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

import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import tqs.backend.dto.RegisterRequest;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

@XrayTest(key = "SCRUM-37")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
        when(userRepository.save(any(User.class))).thenAnswer(i -> (User) i.getArguments()[0]);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        User user = userService.register(request);

        assertNotNull(user);
        assertEquals("João Silva", user.getFullName());
        assertEquals("joao@email.com", user.getEmail());
        assertEquals(UserRole.RENTER, user.getRole());
        assertNotEquals("Senha123", user.getPassword()); // password encriptada
    }

    @Test
    void shouldThrowWhenPasswordsDoNotMatch() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Maria");
        request.setEmail("maria@email.com");
        request.setPassword("Senha123");
        request.setConfirmPassword("Senha321");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
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

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(request));

        assertEquals("Email já está em uso", ex.getMessage());
    }
}
