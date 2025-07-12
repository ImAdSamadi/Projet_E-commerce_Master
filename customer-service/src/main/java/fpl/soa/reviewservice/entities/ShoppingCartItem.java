package fpl.soa.reviewservice.entities;

import fpl.soa.reviewservice.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
@Builder
public class ShoppingCartItem {
    private Product product;
    private int quantity;
    private boolean selected;
}
