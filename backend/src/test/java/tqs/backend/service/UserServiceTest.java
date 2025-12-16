package tqs.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import tqs.backend.dto.RegisterRequest;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

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
    void whenRegisterUser_thenUserIsSavedWithEncodedPasswordHash() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");
        request.setFullName("Test User");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPasswordHash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.register(request);

        assertThat(saved.getEmail()).isEqualTo("test@test.com");
        assertThat(saved.getFullName()).isEqualTo("Test User");
        assertThat(saved.getPasswordHash()).isEqualTo("encodedPasswordHash");
        assertThat(saved.getRole()).isEqualTo(UserRole.RENTER);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void whenRegisterExistingEmail_thenThrows() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("x@test.com");
        request.setFullName("X");
        request.setPassword("Password1");
        request.setConfirmPassword("Password1");

        User existing = User.builder().email("x@test.com").passwordHash("hash").role(UserRole.RENTER).build();
        when(userRepository.findByEmail("x@test.com")).thenReturn(Optional.of(existing));

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> userService.register(request));
    }
}
