package fpl.soa.paymentservice.service.handler;


import fpl.soa.common.commands.PaymentUrlCommand;
import fpl.soa.common.commands.ProcessPaymentCommand;
import fpl.soa.common.events.PaymentFailedEvent;
import fpl.soa.common.events.PaymentProcessedEvent;
import fpl.soa.common.events.PaymentUrlEvent;
import fpl.soa.common.exceptions.CreditCardProcessorUnavailableException;
import fpl.soa.paymentservice.service.PayPalService;
import fpl.soa.paymentservice.service.broadcaster.PaymentEventBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics="${payments.commands.topic.name}")
public class PaymentsCommandsHandler {

    private final PayPalService payPalService;
    private final PaymentEventBroadcaster paymentEventBroadcaster;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String paymentEventsTopicName;

    public PaymentsCommandsHandler(PayPalService payPalService, PaymentEventBroadcaster paymentEventBroadcaster,
                                   KafkaTemplate<String, Object> kafkaTemplate,
                                   @Value("${payments.events.topic.name}") String paymentEventsTopicName) {
        this.payPalService = payPalService;
        this.paymentEventBroadcaster = paymentEventBroadcaster;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentEventsTopicName = paymentEventsTopicName;
    }

    @KafkaHandler
    public void handleCommand(@Payload ProcessPaymentCommand command) {
        try {
            String approvalUrl = payPalService.createPayment(
                    command.getTotalAmount(),
                    "USD",
                    command.getOrderId(),
                    command.getCouponAmount());

            // Build the PaymentUrlEvent
            PaymentUrlEvent paymentUrlEvent = PaymentUrlEvent.builder()
                    .orderId(command.getOrderId())
                    .customerId(command.getCustomerId())
                    .paymentUrl(approvalUrl)
                    .build();

            // ✅ 1. Push to frontend (via SSE)
            paymentEventBroadcaster.publish(
                    PaymentUrlCommand.builder()
                            .orderId(command.getOrderId())
                            .customerId(command.getCustomerId())
                            .paymentUrl(approvalUrl)
                            .build()
            );

            // ✅ 2. Also send to Kafka so next saga step triggers
            kafkaTemplate.send(paymentEventsTopicName, paymentUrlEvent); // << This triggers the next step

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
