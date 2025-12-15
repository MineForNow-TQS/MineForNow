package tqs.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tqs.backend.dto.UpdateProfileRequest;
import tqs.backend.dto.UserProfileResponse;
import tqs.backend.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        UserProfileResponse profile = userService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentUser(@Valid @RequestBody UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        UserProfileResponse updatedProfile = userService.updateUserProfile(email, request);
        return ResponseEntity.ok(updatedProfile);
    }
}
