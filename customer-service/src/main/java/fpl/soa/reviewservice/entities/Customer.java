package fpl.soa.reviewservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
@Builder
@Document
public class Customer {
    @Id
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

//    private List<String> favoris;

    @DBRef
    private ShoppingCart shoppingCart;
}
