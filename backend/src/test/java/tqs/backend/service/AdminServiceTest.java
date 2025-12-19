package tqs.backend.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.backend.model.User;
import tqs.backend.model.UserRole;
import tqs.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    private User pendingUser;

    @BeforeEach
    void setUp() {
        pendingUser = User.builder()
                .id(1L)
                .email("test@user.com")
                .role(UserRole.PENDING_OWNER)
                .build();
    }

    @Test
    void whenApproveRequest_thenUserRoleChangesToOwner() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(pendingUser));

        adminService.approveOwnerRequest(1L);

        assertThat(pendingUser.getRole()).isEqualTo(UserRole.OWNER);
        verify(userRepository, times(1)).save(pendingUser);
    }

    @Test
    void whenRejectRequest_thenUserRoleChangesToRenter() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(pendingUser));

        adminService.rejectOwnerRequest(1L);

        assertThat(pendingUser.getRole()).isEqualTo(UserRole.RENTER);
        verify(userRepository, times(1)).save(pendingUser);
    }

    @Test
    void whenUserNotFound_thenThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            adminService.approveOwnerRequest(99L);
        });
    }
}