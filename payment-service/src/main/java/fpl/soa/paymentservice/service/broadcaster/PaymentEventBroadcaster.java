package fpl.soa.paymentservice.service.broadcaster;

import fpl.soa.common.commands.PaymentUrlCommand;
import fpl.soa.common.events.PaymentUrlEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PaymentEventBroadcaster {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String orderId) {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        emitters.put(orderId, emitter);
        emitter.onCompletion(() -> emitters.remove(orderId));
        emitter.onTimeout(() -> emitters.remove(orderId));
        return emitter;
    }

    public void publish(PaymentUrlCommand paymentUrlCommand) {
        SseEmitter emitter = emitters.get(paymentUrlCommand.getOrderId());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("payment-url")
                        .data(paymentUrlCommand));
                emitter.complete();
            } catch (IOException e) {
                emitters.remove(paymentUrlCommand.getOrderId());
            }
        }
    }
}

