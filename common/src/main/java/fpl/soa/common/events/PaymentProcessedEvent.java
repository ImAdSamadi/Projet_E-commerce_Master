package fpl.soa.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor @Data @Builder
public class PaymentProcessedEvent {
    private String orderId;
    private String customerId;
    private String customerEmailAddress;
    private String originatingAddress;
    private String shippingAddress;
    private String firstName;
    private String lastName;
    private String receiverFullName;
    private String receiverEmail;
    private String couponCode ;
}
