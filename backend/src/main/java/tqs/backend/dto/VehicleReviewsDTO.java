package tqs.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleReviewsDTO {
    private Double averageRating;
    private Long totalReviews;
    private List<ReviewDTO> reviews;
}
