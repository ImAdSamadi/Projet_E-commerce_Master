package fpl.soa.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor @Builder
public class OrderCreatedEvent {
    private String orderId;
    private String customerId;
    private String customerEmail;
    private String shippingAddress;
    private String customerFirstName;
    private String customerLastName;
    private String receiverFullName;
    private String receiverEmail;
    private String couponCode;
}
