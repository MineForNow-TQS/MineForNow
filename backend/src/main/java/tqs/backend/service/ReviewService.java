package tqs.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.backend.dto.ReviewDTO;
import tqs.backend.dto.VehicleReviewsDTO;
import tqs.backend.model.Review;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.ReviewRepository;
import tqs.backend.repository.VehicleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public VehicleReviewsDTO getVehicleReviews(Long vehicleId) {
        // Validate vehicle exists
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        // Get reviews
        List<Review> reviews = reviewRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId);

        // Calculate average rating
        Double averageRating = reviewRepository.calculateAverageRating(vehicleId);
        if (averageRating == null) {
            averageRating = 0.0;
        }

        // Get total count
        Long totalReviews = reviewRepository.countByVehicleId(vehicleId);

        // Convert to DTOs
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new VehicleReviewsDTO(averageRating, totalReviews, reviewDTOs);
    }

    public Double calculateAverageRating(Long vehicleId) {
        Double average = reviewRepository.calculateAverageRating(vehicleId);
        return average != null ? average : 0.0;
    }

    private ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getReviewer().getFullName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt());
    }
}
