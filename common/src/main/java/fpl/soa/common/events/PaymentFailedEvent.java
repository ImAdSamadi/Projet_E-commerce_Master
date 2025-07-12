package fpl.soa.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class PaymentFailedEvent {

    private String orderId;
    private String customerId;
    private String customerEmailAddress;
    private String firstName;
    private String lastName;
    private String receiverFullName;
    private String receiverEmail;

}