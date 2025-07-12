package fpl.soa.ordersservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    private String customerId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String shippingAddress;
    private String city;
    private String state;
    private String zipCode;
    private String password;
    private String profilePictureBase64;

    private ShoppingCart shoppingCart;
}