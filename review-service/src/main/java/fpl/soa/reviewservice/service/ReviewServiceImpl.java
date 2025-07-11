package fpl.soa.reviewservice.service;


import fpl.soa.reviewservice.entities.Review;
import fpl.soa.reviewservice.model.PageInfo;
import fpl.soa.reviewservice.repos.ReviewRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepo reviewRepository;

    public Review addReview(Review review) {
        review.setReviewId(UUID.randomUUID().toString());
        review.setReviewDate(LocalDate.now());
        return reviewRepository.save(review);
    }


    @Override
    public Page<Review> getReviewsByProductId(String productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("reviewDate").descending());
        return reviewRepository.findByProductId(productId, pageable);
    }

    @Override
    public void initReview() {

    }

    @Override
    public PageInfo getProductReviewsPageInfo(int size) {
        return null;
    }


    @Override
    public Review updateReview(Review review) {
        return null;
    }

    @Override
    public void deleteReview(String reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public Map<String, Object> getReviewStats(String productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        int total = reviews.size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", average);
        stats.put("totalReviews", total);
        return stats;
    }
}
