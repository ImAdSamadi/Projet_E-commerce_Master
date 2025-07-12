package fpl.soa.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor @Builder
public class ProductReservedEvent {

    private String orderId;
    private String customerId;

    private String productId;
    private Integer productQuantity;
    private Double productPrice;

    private String customerEmailAddress;
    private String originatingAddress;
    private String shippingAddress;
    private String firstName;
    private String lastName;
    private String receiverFullName;
    private String receiverEmail;

}
