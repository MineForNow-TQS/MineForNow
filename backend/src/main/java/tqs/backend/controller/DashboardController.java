package tqs.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tqs.backend.dto.DashboardStatsDTO;
import tqs.backend.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerDashboard() {
        try {
            // Get authenticated user email from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String ownerEmail = authentication.getName();

            DashboardStatsDTO stats = dashboardService.getOwnerStats(ownerEmail);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/owner/pending-bookings")
    public ResponseEntity<Object> getOwnerPendingBookings() {
        try {
            // Get authenticated user email from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String ownerEmail = authentication.getName();

            var pendingBookings = dashboardService.getPendingBookings(ownerEmail);
            return ResponseEntity.ok(pendingBookings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/owner/pending-bookings")
    public ResponseEntity<Object> getOwnerPendingBookings() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String ownerEmail = authentication.getName();

            var pendingBookings = dashboardService.getPendingBookings(ownerEmail);
            return ResponseEntity.ok(pendingBookings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
