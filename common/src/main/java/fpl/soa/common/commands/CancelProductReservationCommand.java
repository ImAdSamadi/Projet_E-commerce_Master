package fpl.soa.common.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor @Builder
public class CancelProductReservationCommand {
    private String orderId;
    private String productId;
    private String productSize;
    private String productColor;
    private Integer productQuantity;
}
