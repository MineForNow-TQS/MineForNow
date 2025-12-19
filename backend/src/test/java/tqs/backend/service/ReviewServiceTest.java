package tqs.backend.service;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.backend.dto.VehicleReviewsDTO;
import tqs.backend.model.Review;
import tqs.backend.model.User;
import tqs.backend.model.Vehicle;
import tqs.backend.repository.ReviewRepository;
import tqs.backend.repository.VehicleRepository;
import tqs.backend.repository.BookingRepository;
import tqs.backend.repository.UserRepository;
import tqs.backend.dto.CreateReviewDTO;
import tqs.backend.dto.ReviewDTO;
import tqs.backend.model.Booking;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Vehicle vehicle;
    private User reviewer1;
    private User reviewer2;
    private Review review1;
    private Review review2;
    private Review review3;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setBrand("Mercedes");
        vehicle.setModel("AMG GT");

        reviewer1 = new User();
        reviewer1.setId(1L);
        reviewer1.setFullName("João Silva");

        reviewer2 = new User();
        reviewer2.setId(2L);
        reviewer2.setFullName("Maria Santos");

        review1 = new Review(vehicle, reviewer1, 5, "Excelente carro!");
        review1.setId(1L);
        review1.setCreatedAt(LocalDateTime.now().minusDays(5));

        review2 = new Review(vehicle, reviewer2, 4, "Muito bom");
        review2.setId(2L);
        review2.setCreatedAt(LocalDateTime.now().minusDays(2));

        review3 = new Review(vehicle, reviewer1, 5, "Perfeito!");
        review3.setId(3L);
        review3.setCreatedAt(LocalDateTime.now().minusDays(1));
    }

    @Test
    @Requirement("SCRUM-30")
    void getVehicleReviews_WithReviews_ReturnsCorrectAverage() {
        // Given
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(reviewRepository.findByVehicleIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(review3, review2, review1)); // Ordered by date desc
        when(reviewRepository.calculateAverageRating(1L)).thenReturn(4.67);
        when(reviewRepository.countByVehicleId(1L)).thenReturn(3L);

        // When
        VehicleReviewsDTO result = reviewService.getVehicleReviews(1L);

        // Then
        assertThat(result.getAverageRating()).isEqualTo(4.67);
        assertThat(result.getTotalReviews()).isEqualTo(3L);
        assertThat(result.getReviews()).hasSize(3);
        assertThat(result.getReviews().get(0).getRating()).isEqualTo(5);
        assertThat(result.getReviews().get(0).getReviewerName()).isEqualTo("João Silva");
    }

    @Test
    @Requirement("SCRUM-30")
    void getVehicleReviews_NoReviews_ReturnsEmptyList() {
        // Given
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(reviewRepository.findByVehicleIdOrderByCreatedAtDesc(1L))
                .thenReturn(Collections.emptyList());
        when(reviewRepository.calculateAverageRating(1L)).thenReturn(null);
        when(reviewRepository.countByVehicleId(1L)).thenReturn(0L);

        // When
        VehicleReviewsDTO result = reviewService.getVehicleReviews(1L);

        // Then
        assertThat(result.getAverageRating()).isEqualTo(0.0);
        assertThat(result.getTotalReviews()).isEqualTo(0L);
        assertThat(result.getReviews()).isEmpty();
    }

    @Test
    @Requirement("SCRUM-30")
    void getVehicleReviews_VehicleNotFound_ThrowsException() {
        // Given
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.getVehicleReviews(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vehicle not found");
    }

    @Test
    @Requirement("SCRUM-30")
    void getVehicleReviews_MultipleReviews_OrderedByDateDesc() {
        // Given
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(reviewRepository.findByVehicleIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(review3, review2, review1)); // Most recent first
        when(reviewRepository.calculateAverageRating(1L)).thenReturn(4.67);
        when(reviewRepository.countByVehicleId(1L)).thenReturn(3L);

        // When
        VehicleReviewsDTO result = reviewService.getVehicleReviews(1L);

        // Then
        assertThat(result.getReviews()).hasSize(3);
        // Verify order: most recent first
        assertThat(result.getReviews().get(0).getId()).isEqualTo(3L); // review3 (1 day ago)
        assertThat(result.getReviews().get(1).getId()).isEqualTo(2L); // review2 (2 days ago)
        assertThat(result.getReviews().get(2).getId()).isEqualTo(1L); // review1 (5 days ago)
    }

    @Test
    @Requirement("SCRUM-30")
    void calculateAverageRating_WithReviews_ReturnsCorrectValue() {
        // Given
        when(reviewRepository.calculateAverageRating(1L)).thenReturn(4.5);

        // When
        Double average = reviewService.calculateAverageRating(1L);

        // Then
        assertThat(average).isEqualTo(4.5);
    }

    @Test
    @Requirement("SCRUM-30")
    void calculateAverageRating_NoReviews_ReturnsZero() {
        // Given
        when(reviewRepository.calculateAverageRating(1L)).thenReturn(null);

        // When
        Double average = reviewService.calculateAverageRating(1L);

        // Then
        assertThat(average).isEqualTo(0.0);
    }

    @Test
    @Requirement("SCRUM-28")
    void createReview_ValidData_ReturnsCreatedReview() {
        // Given
        CreateReviewDTO createDto = new CreateReviewDTO(5, "Amazing!", 1L);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus("COMPLETED");
        booking.setVehicle(vehicle);
        booking.setRenter(reviewer1);

        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(reviewer1));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> {
            Review r = i.getArgument(0);
            r.setId(10L);
            return r;
        });

        // When
        ReviewDTO result = reviewService.createReview(createDto, "joao@example.com");

        // Then
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Amazing!");
        assertThat(result.getReviewerName()).isEqualTo("João Silva");
    }

    @Test
    @Requirement("SCRUM-28")
    void createReview_BookingNotCompleted_ThrowsException() {
        // Given
        CreateReviewDTO createDto = new CreateReviewDTO(5, "Amazing!", 1L);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus("CONFIRMED"); // Not COMPLETED
        booking.setVehicle(vehicle);
        booking.setRenter(reviewer1);

        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(reviewer1));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(createDto, "joao@example.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("You can only review completed bookings");
    }

    @Test
    @Requirement("SCRUM-28")
    void createReview_UserNotRenter_ThrowsException() {
        // Given
        CreateReviewDTO createDto = new CreateReviewDTO(5, "Amazing!", 1L);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus("COMPLETED");
        booking.setVehicle(vehicle);
        booking.setRenter(reviewer2); // reviewer2 implies booking belongs to someone else

        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(reviewer1));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(createDto, "joao@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("You can only review your own bookings");
    }
}
