package tqs.backend.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import tqs.backend.dto.RegisterRequest;
import tqs.backend.dto.UpdateProfileRequest;
import tqs.backend.dto.UpgradeOwnerRequest;
import tqs.backend.dto.UserProfileResponse;
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

    @Nested
    @DisplayName("Register Tests")
    @Requirement("SCRUM-37")
    class RegisterTests {

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

    @Nested
    @DisplayName("Get User Profile Tests")
    @Requirement("SCRUM-46")
    class GetUserProfileTests {

        @Test
        @DisplayName("Should return user profile when user exists")
        void shouldReturnUserProfileWhenUserExists() {
            User user = User.builder()
                    .id(1L)
                    .fullName("João Silva")
                    .email("joao@email.com")
                    .phone("+351912345678")
                    .drivingLicense("AB123456")
                    .role(UserRole.RENTER)
                    .build();

            when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));

            UserProfileResponse response = userService.getUserProfile("joao@email.com");

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("João Silva", response.getFullName());
            assertEquals("joao@email.com", response.getEmail());
            assertEquals("+351912345678", response.getPhone());
            assertEquals("AB123456", response.getDrivingLicense());
            assertEquals(UserRole.RENTER, response.getRole());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.getUserProfile("unknown@email.com"));

            assertEquals("Utilizador não encontrado", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Update User Profile Tests")
    @Requirement("SCRUM-46")
    class UpdateUserProfileTests {

        @Test
        @DisplayName("Should update phone and driving license successfully")
        void shouldUpdateProfileSuccessfully() {
            User existingUser = User.builder()
                    .id(1L)
                    .fullName("João Silva")
                    .email("joao@email.com")
                    .phone(null)
                    .drivingLicense(null)
                    .role(UserRole.RENTER)
                    .build();

            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .phone("+351912345678")
                    .drivingLicense("AB123456")
                    .build();

            when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenAnswer(i -> (User) i.getArguments()[0]);

            UserProfileResponse response = userService.updateUserProfile("joao@email.com", request);

            assertNotNull(response);
            assertEquals("+351912345678", response.getPhone());
            assertEquals("AB123456", response.getDrivingLicense());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should only update phone when only phone provided")
        void shouldUpdateOnlyPhone() {
            User existingUser = User.builder()
                    .id(1L)
                    .fullName("João Silva")
                    .email("joao@email.com")
                    .phone(null)
                    .drivingLicense("OLD123")
                    .role(UserRole.RENTER)
                    .build();

            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .phone("+351999888777")
                    .drivingLicense(null)
                    .build();

            when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenAnswer(i -> (User) i.getArguments()[0]);

            UserProfileResponse response = userService.updateUserProfile("joao@email.com", request);

            assertEquals("+351999888777", response.getPhone());
            assertEquals("OLD123", response.getDrivingLicense()); // unchanged
        }

        @Test
        @DisplayName("Should throw exception when user not found for update")
        void shouldThrowWhenUserNotFoundForUpdate() {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .phone("+351912345678")
                    .build();

            when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.updateUserProfile("unknown@email.com", request));

            assertEquals("Utilizador não encontrado", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Request Owner Upgrade Tests")
    @Requirement("SCRUM-47")
    class RequestOwnerUpgradeTests {

        @Test
        @DisplayName("Should upgrade user to pending owner successfully")
        void shouldUpgradeUserSuccessfully() {
            User existingUser = User.builder()
                    .id(1L)
                    .fullName("João Silva")
                    .email("joao@email.com")
                    .role(UserRole.RENTER)
                    .build();

            UpgradeOwnerRequest request = new UpgradeOwnerRequest();
            request.setPhone("+351912345678");
            request.setCitizenCardNumber("12345678");
            request.setDrivingLicense("AB123456");
            request.setMotivation("Quero ser proprietário");

            when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenAnswer(i -> (User) i.getArguments()[0]);

            userService.requestOwnerUpgrade("joao@email.com", request);

            assertEquals(UserRole.PENDING_OWNER, existingUser.getRole());
            assertEquals("+351912345678", existingUser.getPhone());
            assertEquals("12345678", existingUser.getCitizenCardNumber());
            assertEquals("AB123456", existingUser.getDrivingLicense());
            assertEquals("Quero ser proprietário", existingUser.getOwnerMotivation());
            verify(userRepository).save(existingUser);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowWhenUserNotFound() {
            UpgradeOwnerRequest request = new UpgradeOwnerRequest();
            request.setPhone("+351912345678");
            request.setCitizenCardNumber("12345678");
            request.setDrivingLicense("AB123456");
            request.setMotivation("Motivação");

            when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.requestOwnerUpgrade("unknown@email.com", request));

            assertEquals("Utilizador não encontrado", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when user already owner")
        void shouldThrowWhenUserAlreadyOwner() {
            User existingUser = User.builder()
                    .id(1L)
                    .email("owner@email.com")
                    .role(UserRole.OWNER)
                    .build();

            UpgradeOwnerRequest request = new UpgradeOwnerRequest();
            request.setPhone("+351912345678");
            request.setCitizenCardNumber("12345678");
            request.setDrivingLicense("AB123456");
            request.setMotivation("Motivação");

            when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(existingUser));

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> userService.requestOwnerUpgrade("owner@email.com", request));

            assertEquals("Pedido já submetido ou utilizador já é Owner", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when user already pending owner")
        void shouldThrowWhenUserAlreadyPendingOwner() {
            User existingUser = User.builder()
                    .id(1L)
                    .email("pending@email.com")
                    .role(UserRole.PENDING_OWNER)
                    .build();

            UpgradeOwnerRequest request = new UpgradeOwnerRequest();
            request.setPhone("+351912345678");
            request.setCitizenCardNumber("12345678");
            request.setDrivingLicense("AB123456");
            request.setMotivation("Motivação");

            when(userRepository.findByEmail("pending@email.com")).thenReturn(Optional.of(existingUser));

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> userService.requestOwnerUpgrade("pending@email.com", request));

            assertEquals("Pedido já submetido ou utilizador já é Owner", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Approve Owner Request Tests")
    @Requirement("SCRUM-25")
    class ApproveOwnerRequestTests {

        @Test
        @DisplayName("Should approve owner request successfully")
        void shouldApproveOwnerRequestSuccessfully() {
            User pendingOwner = User.builder()
                    .id(1L)
                    .email("pending@email.com")
                    .role(UserRole.PENDING_OWNER)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(pendingOwner));
            when(userRepository.save(any(User.class))).thenAnswer(i -> (User) i.getArguments()[0]);

            userService.approveOwnerRequest(1L);

            assertEquals(UserRole.OWNER, pendingOwner.getRole());
            verify(userRepository).save(pendingOwner);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.approveOwnerRequest(1L));

            assertEquals("Utilizador não encontrado", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when user not pending owner")
        void shouldThrowWhenUserNotPending() {
            User renter = User.builder()
                    .id(1L)
                    .role(UserRole.RENTER)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(renter));

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> userService.approveOwnerRequest(1L));

            assertEquals("Utilizador não tem pedido pendente", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Reject Owner Request Tests")
    @Requirement("SCRUM-25")
    class RejectOwnerRequestTests {

        @Test
        @DisplayName("Should reject owner request successfully")
        void shouldRejectOwnerRequestSuccessfully() {
            User pendingOwner = User.builder()
                    .id(1L)
                    .email("pending@email.com")
                    .role(UserRole.PENDING_OWNER)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(pendingOwner));
            when(userRepository.save(any(User.class))).thenAnswer(i -> (User) i.getArguments()[0]);

            userService.rejectOwnerRequest(1L);

            assertEquals(UserRole.RENTER, pendingOwner.getRole());
            verify(userRepository).save(pendingOwner);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.rejectOwnerRequest(1L));

            assertEquals("Utilizador não encontrado", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when user not pending owner")
        void shouldThrowWhenUserNotPending() {
            User renter = User.builder()
                    .id(1L)
                    .role(UserRole.RENTER)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(renter));

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> userService.rejectOwnerRequest(1L));

            assertEquals("Utilizador não tem pedido pendente", ex.getMessage());
        }
    }

}
