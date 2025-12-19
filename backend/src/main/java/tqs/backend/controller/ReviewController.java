package tqs.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.backend.dto.VehicleReviewsDTO;
import tqs.backend.service.ReviewService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/vehicles/{id}/reviews")
    public ResponseEntity<Object> getVehicleReviews(@PathVariable Long id) {
        try {
            VehicleReviewsDTO reviews = reviewService.getVehicleReviews(id);
            return ResponseEntity.ok(reviews);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PostMapping("/reviews")
    public ResponseEntity<Object> createReview(@RequestBody tqs.backend.dto.CreateReviewDTO createReviewDTO) {
        try {
            // Get authenticated user email from JWT
            // Note: In real setup, we should use SecurityContext.
            // Assuming SecurityConfig is set up, we access it here.
            org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            String userEmail = authentication.getName();

            tqs.backend.dto.ReviewDTO createdReview = reviewService.createReview(createReviewDTO, userEmail);
            return ResponseEntity.status(201).body(createdReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage()); // Conflict for state issues
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}
