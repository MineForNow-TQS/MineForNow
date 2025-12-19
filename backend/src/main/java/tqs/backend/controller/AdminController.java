package tqs.backend.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tqs.backend.dto.AdminStatsDTO;
import tqs.backend.dto.UpgradeOwnerRequest;
import tqs.backend.dto.UserProfileResponse;
import tqs.backend.service.AdminService;
import tqs.backend.service.UserService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @PostMapping("/upgrade")
    public ResponseEntity<Void> requestOwnerUpgrade(
            @Valid @RequestBody UpgradeOwnerRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        userService.requestOwnerUpgrade(email, request);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/{id}/approve-owner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveOwnerRequest(@PathVariable Long id) {
        adminService.approveOwnerRequest(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/reject-owner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectOwnerRequest(@PathVariable Long id) {
        adminService.rejectOwnerRequest(id);
        return ResponseEntity.ok().build();
    }
}
