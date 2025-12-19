package tqs.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tqs.backend.dto.OwnerRequestDTO;
import tqs.backend.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") 
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/requests/pending")
    public ResponseEntity<List<OwnerRequestDTO>> getPendingRequests() {
        return ResponseEntity.ok(adminService.getPendingOwnerRequests());
    }

    @PutMapping("/requests/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        adminService.approveOwnerRequest(id);
        return ResponseEntity.ok(Map.of("message", "Utilizador aprovado como Owner com sucesso"));
    }

    @PutMapping("/requests/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        adminService.rejectOwnerRequest(id);
        return ResponseEntity.ok(Map.of("message", "Pedido rejeitado com sucesso"));
    }
}