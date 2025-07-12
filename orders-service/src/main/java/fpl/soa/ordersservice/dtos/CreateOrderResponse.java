package fpl.soa.ordersservice.dtos;


import fpl.soa.common.types.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor @Builder
public class CreateOrderResponse {
    private String orderId;
    private String customerId;
    private OrderStatus status;
    private String shippingAddress;
    private Date createdAt;
}
