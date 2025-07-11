package fpl.soa.reviewservice.model;

import lombok.Data;

@Data
public class AddItemRequest {

    private String productId ;
    private String customerId ;
    private int quantity;
    private String pickedSize;
    private String pickedColor ;

}
