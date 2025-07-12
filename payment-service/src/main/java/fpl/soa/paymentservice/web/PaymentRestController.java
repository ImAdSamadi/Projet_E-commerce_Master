package fpl.soa.paymentservice.web;


import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import fpl.soa.common.events.PaymentFailedEvent;
import fpl.soa.common.events.PaymentProcessedEvent;
import fpl.soa.paymentservice.model.CaptureRequest;
import fpl.soa.paymentservice.service.PayPalService;
import fpl.soa.paymentservice.service.broadcaster.PaymentEventBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentRestController {

    private final PaymentEventBroadcaster paymentEventBroadcaster;
    private final PayPalService payPalService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${payments.events.topic.name}")
    private String paymentEventsTopicName;

    @GetMapping(value = "/{orderId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter listenForApprovalUrl(@PathVariable String orderId) {
        return paymentEventBroadcaster.subscribe(orderId);
    }


    @PostMapping("/capture")
    public ResponseEntity<?> capturePayment(@RequestBody CaptureRequest request) {
        if (request.getPayerId() == null || request.getPayerId().isBlank()) {
            PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                    .orderId(request.getOrderId())
                    .customerId(request.getCustomerId())
                    .customerEmailAddress(request.getCustomerEmailAddress())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .receiverFullName(request.getReceiverFullName())
                    .receiverEmail(request.getReceiverEmail())
                    .build();

            kafkaTemplate.send(paymentEventsTopicName, failedEvent);
            return ResponseEntity.badRequest().body(Map.of("message", "Payment was cancelled by the user."));
        }

        try {
            Payment payment = payPalService.capturePayment(request.getPaymentId(), request.getPayerId());

            if ("approved".equalsIgnoreCase(payment.getState())) {
                PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                        .orderId(request.getOrderId())
                        .customerId(request.getCustomerId())
                        .customerEmailAddress(request.getCustomerEmailAddress())
                        .originatingAddress(request.getOriginatingAddress())
                        .shippingAddress(request.getShippingAddress())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .receiverFullName(request.getReceiverFullName())
                        .receiverEmail(request.getReceiverEmail())
                        .build();

                kafkaTemplate.send(paymentEventsTopicName, event);

                return ResponseEntity.ok(Map.of("message", "Payment captured and event sent."));
            } else {
                PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                        .orderId(request.getOrderId())
                        .customerId(request.getCustomerId())
                        .customerEmailAddress(request.getCustomerEmailAddress())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .receiverFullName(request.getReceiverFullName())
                        .receiverEmail(request.getReceiverEmail())
                        .build();

                kafkaTemplate.send(paymentEventsTopicName, failedEvent);

                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                        .body(Map.of("message", "Payment was not approved."));
            }

        } catch (PayPalRESTException e) {
            PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                    .orderId(request.getOrderId())
                    .customerId(request.getCustomerId())
                    .customerEmailAddress(request.getCustomerEmailAddress())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .receiverFullName(request.getReceiverFullName())
                    .receiverEmail(request.getReceiverEmail())
                    .build();

            kafkaTemplate.send(paymentEventsTopicName, failedEvent);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Payment capture failed", "error", e.getMessage()));
        }
    }




}
