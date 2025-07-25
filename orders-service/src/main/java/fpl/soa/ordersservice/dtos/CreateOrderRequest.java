package fpl.soa.ordersservice.dtos;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Data @NoArgsConstructor
@AllArgsConstructor @Builder
public class CreateOrderRequest {

    @NotNull
    private String customerId;
    @NotNull
    private String shippingAddress;

    private String receiverFullName;
    private String receiverEmail;
    private String couponCode;

}