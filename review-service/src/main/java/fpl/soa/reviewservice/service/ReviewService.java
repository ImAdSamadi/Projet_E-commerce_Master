package fpl.soa.reviewservice.service;

import fpl.soa.reviewservice.entities.Review;
import fpl.soa.reviewservice.model.PageInfo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ReviewService {

    Review addReview(Review review);
    Review updateReview(Review review);
    void deleteReview(String reviewId);

    Page<Review> getReviewsByProductId(String productId, int page, int size);
    void initReview() ;
    PageInfo getProductReviewsPageInfo(int size);

    Map<String, Object> getReviewStats(String productId);

}
