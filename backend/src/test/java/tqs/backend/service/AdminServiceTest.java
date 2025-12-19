package tqs.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import tqs.backend.dto.AdminStatsDTO;
import tqs.backend.dto.UserProfileResponse;
import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.repository.VehicleRepository;

@XrayTest(key = "SCRUM-25")
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Dashboard Stats Tests")
    @Requirement("SCRUM-75")
    class DashboardStatsTests {

        @Test
        @DisplayName("Should return correct dashboard statistics")
        void shouldReturnCorrectDashboardStats() {
            when(userRepository.count()).thenReturn(10L);
            when(vehicleRepository.count()).thenReturn(5L);
            when(bookingRepository.count()).thenReturn(20L);
            when(bookingRepository.sumTotalPrice()).thenReturn(1500.0);

            AdminStatsDTO stats = adminService.getDashboardStats();

            assertNotNull(stats);
            assertEquals(10L, stats.getTotalUsers());
            assertEquals(5L, stats.getTotalCars());
            assertEquals(20L, stats.getTotalBookings());
            assertEquals(1500.0, stats.getTotalRevenue());
        }

        @Test
        @DisplayName("Should return zero revenue when no bookings exist")
        void shouldReturnZeroRevenueWhenNoBookings() {
            when(userRepository.count()).thenReturn(10L);
            when(vehicleRepository.count()).thenReturn(5L);
            when(bookingRepository.count()).thenReturn(0L);
            when(bookingRepository.sumTotalPrice()).thenReturn(null);

            AdminStatsDTO stats = adminService.getDashboardStats();

            assertNotNull(stats);
            assertEquals(0.0, stats.getTotalRevenue());
        }
    }

    @Nested
    @DisplayName("Get All Users Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() {
            User user1 = User.builder().id(1L).fullName("User 1").email("user1@test.com").role(UserRole.RENTER).build();
            User user2 = User.builder().id(2L).fullName("User 2").email("user2@test.com").role(UserRole.OWNER).build();

            when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

            List<UserProfileResponse> users = adminService.getAllUsers();

            assertNotNull(users);
            assertEquals(2, users.size());
            assertEquals("User 1", users.get(0).getFullName());
            assertEquals("User 2", users.get(1).getFullName());
        }
    }

    @Nested
    @DisplayName("Approve Owner Request Tests")
    @Requirement("SCRUM-25")
    class ApproveOwnerRequestTests {

        @SuppressWarnings("null")
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

            adminService.approveOwnerRequest(1L);

            assertEquals(UserRole.OWNER, pendingOwner.getRole());
            verify(userRepository).save(pendingOwner);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> adminService.approveOwnerRequest(1L));

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
                    () -> adminService.approveOwnerRequest(1L));

            assertEquals("Utilizador não tem pedido pendente", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Reject Owner Request Tests")
    @Requirement("SCRUM-25")
    class RejectOwnerRequestTests {

        @SuppressWarnings("null")
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

            adminService.rejectOwnerRequest(1L);

            assertEquals(UserRole.RENTER, pendingOwner.getRole());
            verify(userRepository).save(pendingOwner);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> adminService.rejectOwnerRequest(1L));

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
                    () -> adminService.rejectOwnerRequest(1L));

            assertEquals("Utilizador não tem pedido pendente", ex.getMessage());
        }
    }
}
