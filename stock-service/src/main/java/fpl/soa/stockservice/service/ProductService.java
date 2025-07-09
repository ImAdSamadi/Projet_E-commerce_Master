package fpl.soa.stockservice.service;

import fpl.soa.stockservice.DTO.ProductVariantDetails;
import fpl.soa.stockservice.entities.Category;
import fpl.soa.stockservice.entities.PageInfo;
import fpl.soa.stockservice.entities.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {


    List<Product> findAll();
    Product reserve(Product desiredProduct, String orderId);
    void cancelReservation(Product productToCancel, String orderId);

    Product createProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(String productId);
    Product getProductById(String productId) ;
    Page<Product> getProductsByCategoryId(String category, int page, int size);
    List<Product> getProductsByName(String name) ;
    List<Product> getAllProduct();
    Page<Product> getPaginatedProducts(int page , int size);
    void initProduct() ;
    PageInfo getProductPageInfo(int size);

    ProductVariantDetails getProductBySizeAndColor(String productId, String size, String color);

}
