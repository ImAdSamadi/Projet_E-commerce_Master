package fpl.soa.stockservice.service.handler;

import fpl.soa.common.commands.CancelProductReservationCommand;
import fpl.soa.common.commands.ReserveProductCommand;
import fpl.soa.common.events.ProductReservationCancelledEvent;
import fpl.soa.common.events.ProductReservationFailedEvent;
import fpl.soa.common.events.ProductReservedEvent;
import fpl.soa.stockservice.entities.ColorVariant;
import fpl.soa.stockservice.entities.Product;
import fpl.soa.stockservice.entities.SizeVariant;
import fpl.soa.stockservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@KafkaListener(topics = "${products.commands.topic.name}")
public class ProductCommandsHandler {

    private final ProductService productService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String productEventsTopicName;

    public ProductCommandsHandler(ProductService productService,
                                  KafkaTemplate<String, Object> kafkaTemplate,
                                  @Value("${products.events.topic.name}") String productEventsTopicName) {
        this.productService = productService;
        this.kafkaTemplate = kafkaTemplate;
        this.productEventsTopicName = productEventsTopicName;
    }

    @KafkaHandler
    public void handleCommand(@Payload ReserveProductCommand command) {

        System.out.println("...Reserving Product ID: "+command.getProductId()
                +" Size: "+command.getProductSize()+" Color: "+command.getProductColor()
                +" Quantity: "+command.getProductQuantity());

        try {
            SizeVariant sizeVariant = SizeVariant.builder()
                    .size(command.getProductSize())  // Make sure this getter exists
                    .colorVariants(List.of(
                            ColorVariant.builder()
                                    .color(command.getProductColor())  // Make sure this getter exists
                                    .quantity(command.getProductQuantity())
                                    .build()
                    ))
                    .build();

            Product desiredProduct = Product.builder()
                    .productId(command.getProductId())
                    .sizeVariants(List.of(sizeVariant))
                    .build();

            Product reservedProduct = productService.reserve(desiredProduct, command.getOrderId());

            double productPrice = 0.0;

            if (reservedProduct.getSizeVariants() != null) {
                Optional<SizeVariant> matchingSizeVariant = reservedProduct.getSizeVariants().stream()
                        .filter(sv -> sv.getSize().equalsIgnoreCase(command.getProductSize()))
                        .findFirst();

                if (matchingSizeVariant.isPresent() && matchingSizeVariant.get().getProductPrice() != null) {
                    productPrice = matchingSizeVariant.get().getProductPrice().getPrice();
                }
            }


            ProductReservedEvent productReservedEvent = ProductReservedEvent.builder()
                    .orderId(command.getOrderId())
                    .customerId(command.getCustomerId())

                    .productId(command.getProductId())
                    .productQuantity(command.getProductQuantity())
                    .productPrice(productPrice)

                    .customerEmailAddress(command.getCustomerEmailAddress())
                    .shippingAddress(command.getShippingAddress())
                    .originatingAddress(command.getOriginatingAddress())
                    .firstName(command.getFirstName())
                    .lastName(command.getLastName())
                    .receiverFullName(command.getReceiverFullName())
                    .receiverEmail(command.getReceiverEmail())
                    .couponCode(command.getCouponCode())

                    .build();


            kafkaTemplate.send(productEventsTopicName, productReservedEvent);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            ProductReservationFailedEvent productReservationFailedEvent = new ProductReservationFailedEvent(command.getProductId(),
                    command.getOrderId(), command.getProductQuantity());
            kafkaTemplate.send(productEventsTopicName, productReservationFailedEvent);
        }
    }


    @KafkaHandler
    public void handleCommand(@Payload CancelProductReservationCommand command) {

        SizeVariant sizeVariant = SizeVariant.builder()
                .size(command.getProductSize())  // Make sure this getter exists
                .colorVariants(List.of(
                        ColorVariant.builder()
                                .color(command.getProductColor())  // Make sure this getter exists
                                .quantity(command.getProductQuantity())
                                .build()
                ))
                .build();

        Product productToCancel = Product.builder()
                .productId(command.getProductId())
                .sizeVariants(List.of(sizeVariant))
                .build();

        productService.cancelReservation(productToCancel, command.getOrderId());

        ProductReservationCancelledEvent productReservationCancelledEvent =
                new ProductReservationCancelledEvent(command.getProductId(), command.getOrderId());

        kafkaTemplate.send(productEventsTopicName, productReservationCancelledEvent);
    }


}
