package fpl.soa.ordersservice.entities;


import fpl.soa.common.types.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;


@Document
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class OrderEntity {
    @Id
    private String orderId;
    private String customerId;
    private List<OrderItem> products;
    private Double totalPrice;

    private OrderStatus status;
    private Date createdAt;
    private Date updatedAt;

    private String shippingAddress;

}
