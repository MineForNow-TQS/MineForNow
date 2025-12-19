package tqs.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.backend.dto.VehicleReviewsDTO;
import tqs.backend.service.ReviewService;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{id}/reviews")
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
}
