package fpl.soa.common.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor @Data @Builder
public class ReserveProductCommand {

    private String orderId;
    private String customerId;

    private String productId;
    private Integer productQuantity;
    private String productSize;    // NEW: size variant to reserve
    private String productColor;   // NEW: color variant to reserve

    private String customerEmailAddress;
    private String originatingAddress;
    private String shippingAddress;
    private String firstName;
    private String lastName;
    private String receiverFullName;
    private String receiverEmail;
    private String couponCode;
}
