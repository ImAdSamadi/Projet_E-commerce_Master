package fpl.soa.common.commands;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor @Builder
public class PaymentUrlCommand {

    private String orderId;
    private String customerId;
    private String paymentId;
    private String paymentUrl;

}
