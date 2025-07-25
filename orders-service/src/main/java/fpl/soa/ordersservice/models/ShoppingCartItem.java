package fpl.soa.ordersservice.models;

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
