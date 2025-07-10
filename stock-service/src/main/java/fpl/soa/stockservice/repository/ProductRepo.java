package fpl.soa.stockservice.repository;

import fpl.soa.stockservice.entities.Category;
import fpl.soa.stockservice.entities.Product;
import fpl.soa.stockservice.projections.ProductsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(excerptProjection = ProductsProjection.class)
public interface ProductRepo extends MongoRepository<Product , String>, ProductCustomRepository {
//    Page<Product> findByStatus(String status, Pageable pageable);
//    Page<Product> findByCategoryId(String category, Pageable pageable);
//    Page<Product> findByNameContainsIgnoreCase(String keyword, Pageable pageable);
//    Page<Product> findBySelected(Boolean selected, Pageable pageable);
//    Product findByProductId(String productId);

    Page<Product> findByStatus(String status, Pageable pageable);
    Page<Product> findByCategoryId(String category, Pageable pageable);
    Page<Product> findByNameContainsIgnoreCase(String keyword, Pageable pageable);
    Product findByProductId(String productId);

    // ✅ Custom query to search nested 'selected' field
    @Query("{ 'sizeVariants.colorVariants.selected': ?0 }")
    Page<Product> findBySelected(Boolean selected, Pageable pageable);

}