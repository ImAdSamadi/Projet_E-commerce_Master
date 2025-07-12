package fpl.soa.ordersservice.entities;

import fpl.soa.ordersservice.models.Price;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {

    private String productId ;
    private String name;
    private Price priceAtPurchase;
    private Integer quantity;
    private String originLocation;
    private String pickedColor ;
    private String pickedSize ;
    private List<String> productImagesBase64;

}
