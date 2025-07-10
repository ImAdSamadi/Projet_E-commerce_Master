package fpl.soa.stockservice.repository;


import fpl.soa.stockservice.entities.Product;
import fpl.soa.stockservice.filters.ProductFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {

    Page<Product> filterProductsByCategoryWithVariants(ProductFilterRequest request, Pageable pageable);

}
