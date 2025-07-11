package fpl.soa.reviewservice.repos;

import fpl.soa.reviewservice.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ReviewRepo extends MongoRepository<Review, String> {

    Page<Review> findByProductId(String productId, Pageable pageable);

    List<Review> findByProductId(String productId);

}