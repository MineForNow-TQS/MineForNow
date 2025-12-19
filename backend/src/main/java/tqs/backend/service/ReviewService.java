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

    @Autowired
    private tqs.backend.repository.BookingRepository bookingRepository;

    @Autowired
    private tqs.backend.repository.UserRepository userRepository;

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

    public ReviewDTO createReview(tqs.backend.dto.CreateReviewDTO createReviewDTO, String userEmail) {
        // Find user
        tqs.backend.model.User reviewer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find booking
        tqs.backend.model.Booking booking = bookingRepository.findById(createReviewDTO.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Validate booking belongs to user
        if (!booking.getRenter().getId().equals(reviewer.getId())) {
            throw new IllegalArgumentException("You can only review your own bookings");
        }

        // Validate booking is completed
        // Note: checking string status for now, ideally should use Enum
        if (!"COMPLETED".equalsIgnoreCase(booking.getStatus()) && !"CONCLUÍDO".equalsIgnoreCase(booking.getStatus())) {
            // Allow 'CONFIRMED' for testing if 'COMPLETED' is not yet
            // implemented/transitioned automatically,
            // BUT requirements say COMPLETED.
            // IF the system doesn't auto-complete bookings, this might block testing.
            // Assumption: The user will manually set status or we allow CONFIRMED for now?
            // Requirement says: "reserva com estado COMPLETED".
            // checking actual status value.
            throw new IllegalStateException("You can only review completed bookings");
        }

        // Check if review already exists for this booking?
        // Current requirement doesn't explicitly forbid multiple reviews, but it's good
        // practice.
        // Skipping check for now to follow MVP.

        // Create review
        Review review = new Review();
        review.setVehicle(booking.getVehicle());
        review.setReviewer(reviewer);
        review.setRating(createReviewDTO.getRating());
        review.setComment(createReviewDTO.getComment());
        review.setCreatedAt(java.time.LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        return convertToDTO(savedReview);
    }
}
