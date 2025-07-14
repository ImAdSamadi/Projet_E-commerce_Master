package fpl.soa.paymentservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptureRequest {

    private String paymentId;
    private String payerId;
    private String orderId;
    private String customerId;
    private String customerEmailAddress;
    private String originatingAddress;
    private String shippingAddress;
    private String firstName;
    private String lastName;
    private String receiverFullName;
    private String receiverEmail;
    private String couponCode;

}
