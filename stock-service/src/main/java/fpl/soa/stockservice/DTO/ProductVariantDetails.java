package fpl.soa.stockservice.DTO;

import fpl.soa.stockservice.entities.Price;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ProductVariantDetails {
    private String productId;
    private String name;
    private Price productPrice;
    private String originLocation;
    private List<String> productImagesBas64;
}