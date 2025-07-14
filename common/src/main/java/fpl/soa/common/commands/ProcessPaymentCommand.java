package fpl.soa.common.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class ProcessPaymentCommand {

    private String orderId;
    private String customerId;

    private Double totalAmount;

    private String customerEmailAddress;
    private String originatingAddress;
    private String shippingAddress;
    private String firstName;
    private String lastName;
    private String receiverFullName;
    private String receiverEmail;
    private String couponCode;
    private Double couponAmount;

}
