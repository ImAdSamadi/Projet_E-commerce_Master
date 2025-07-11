package fpl.soa.reviewservice.web;

import fpl.soa.reviewservice.entities.Review;
import fpl.soa.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewRestController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        return ResponseEntity.ok(reviewService.addReview(review));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Page<Review>> getReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId, page, size));
    }

    @GetMapping("/stats/{productId}")
    public ResponseEntity<Map<String, Object>> getReviewStats(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getReviewStats(productId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}

