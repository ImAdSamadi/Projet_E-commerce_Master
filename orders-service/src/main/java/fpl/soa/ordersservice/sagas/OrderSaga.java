package fpl.soa.ordersservice.sagas;

import fpl.soa.common.commands.*;
import fpl.soa.common.events.*;
import fpl.soa.common.types.OrderStatus;
import fpl.soa.ordersservice.entities.OrderEntity;
import fpl.soa.ordersservice.entities.OrderItem;
import fpl.soa.ordersservice.service.OrderHistoryService;
import fpl.soa.ordersservice.service.OrdersService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@KafkaListener(topics={
        "${orders.events.topic.name}" ,
        "${products.events.topic.name}",
        "${payments.events.topic.name}",
        "${shipment.event.topic.name}"
})
public class OrderSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String productsCommandsTopicName;
    private final OrderHistoryService orderHistoryService;
    private final String shipmentCommandsTopicName;
    private final String paymentsCommandsTopicName;
    private OrdersService ordersService ;
    private String ordersCommandsTopicName ;

    public OrderSaga(KafkaTemplate<String, Object> kafkaTemplate,
                     @Value("${products.commands.topic.name}") String productsCommandsTopicName,
                     OrderHistoryService orderHistoryService, @Value("${shipment.commands.topic.name}") String shipmentCommandsTopicName,
                     @Value("${payments.commands.topic.name}") String paymentsCommandsTopicName, OrdersService ordersService, @Value("${orders.commands.topic.name}") String ordersCommandsTopicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.productsCommandsTopicName = productsCommandsTopicName;
        this.orderHistoryService = orderHistoryService;
        this.shipmentCommandsTopicName = shipmentCommandsTopicName;
        this.paymentsCommandsTopicName = paymentsCommandsTopicName;
        this.ordersService = ordersService;
        this.ordersCommandsTopicName = ordersCommandsTopicName;
    }

    private final Map<String, Integer> expectedProductCountMap = new ConcurrentHashMap<>();
    private final Map<String, List<ProductReservedEvent>> receivedReservedEventsMap = new ConcurrentHashMap<>();

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedEvent event) {
        System.out.println("***** SAGA step 1 : OrderCreated / orderId : " + event.getOrderId() + " ************* ");

        // Fetch the full order with its products from the database
        OrderEntity order = ordersService.getOrderById(event.getOrderId());
        List<OrderItem> items = order.getProducts();

        // Store how many product reservations we are expecting
        expectedProductCountMap.put(order.getOrderId(), items.size());

        for (OrderItem item : items) {
            ReserveProductCommand command = ReserveProductCommand.builder()
                    .orderId(order.getOrderId())
                    .customerId(order.getCustomerId())

                    .productId(item.getProductId())
                    .productQuantity(item.getQuantity())
                    .productSize(item.getPickedSize())
                    .productColor(item.getPickedColor())

                    .customerEmailAddress(event.getCustomerEmail()) // still from event
                    .shippingAddress(order.getShippingAddress())
                    .originatingAddress(item.getOriginLocation())
                    .firstName(event.getCustomerFirstName())  // assuming from event
                    .lastName(event.getCustomerLastName())    // assuming from event
                    .receiverFullName(event.getReceiverFullName())
                    .receiverEmail(event.getReceiverEmail())

                    .build();

            kafkaTemplate.send(productsCommandsTopicName, command);
        }

        orderHistoryService.add(order.getOrderId(), OrderStatus.CREATED);

    }


    @KafkaHandler
    public void handleEvent(@Payload ProductReservedEvent event) {
        String orderId = event.getOrderId();
        System.out.println("***** SAGA step 2 : ProductReserved ID: "+event.getProductId()+" / orderId : " + orderId + " ************* ");

        // Add the current event to the list for this order
        receivedReservedEventsMap.computeIfAbsent(orderId, k -> new ArrayList<>()).add(event);

        // Check if we have received all expected reservations
        int expectedCount = expectedProductCountMap.getOrDefault(orderId, 0);
        int receivedCount = receivedReservedEventsMap.get(orderId).size();

        if (receivedCount == expectedCount) {
            List<ProductReservedEvent> reservedEvents = receivedReservedEventsMap.remove(orderId);

            // Compute total payment
            double totalAmount = reservedEvents.stream()
                    .mapToDouble(e -> e.getProductPrice() * e.getProductQuantity())
                    .sum();

            // Get shared customer info from one of the events
            ProductReservedEvent any = reservedEvents.get(0);

            ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                    .orderId(orderId)
                    .customerId(any.getCustomerId())
                    .totalAmount(totalAmount)

                    .customerEmailAddress(any.getCustomerEmailAddress())
                    .shippingAddress(any.getShippingAddress())
                    .originatingAddress(any.getOriginatingAddress())
                    .firstName(any.getFirstName())
                    .lastName(any.getLastName())
                    .receiverFullName(any.getReceiverFullName())
                    .receiverEmail(any.getReceiverEmail())

                    .build();

            kafkaTemplate.send(paymentsCommandsTopicName, processPaymentCommand);

            // Clean up the count map
            // expectedProductCountMap.remove(orderId);
        }
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentUrlEvent event){
        System.out.println("***** SAGA step : PaymentUrl : "+event.getPaymentUrl()
                +" / orderId : " + event.getOrderId() + " ************* ");
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentProcessedEvent event){
        System.out.println("***** SAGA step 3 : PaymentProcessed / orderId :  " + event.getOrderId() + " ************* ");
        InitiateShipmentCommand initiateShipmentCommand = InitiateShipmentCommand.builder()
                .orderId(event.getOrderId())
                .customerEmailAddress(event.getCustomerEmailAddress())
                .shippingAddress(event.getShippingAddress())
                .originatingAddress(event.getOriginatingAddress())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .build();
        kafkaTemplate.send(shipmentCommandsTopicName,initiateShipmentCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload ShipmentInProgressEvent event){
        System.out.println("***** SAGA step 4 : ShipmentInProgress / orderId : " + event.getOrderId() + " ************* ");
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(event.getOrderId());
        kafkaTemplate.send(ordersCommandsTopicName,approveOrderCommand);
    }
    @KafkaHandler
    public void handleEvent(@Payload OrderApprovedEvent event) {
        System.out.println("***** SAGA step 5 : OrderApproved / orderId : " + event.getOrderId() + " ************* ");
        orderHistoryService.add(event.getOrderId(), OrderStatus.APPROVED);
    }

    /** roll back transaction **/
    @KafkaHandler
    public void handleEvent(@Payload PaymentFailedEvent event) {
        System.out.println("***** SAGA rollback transaction : PaymentFailedEvent / orderId : " + event.getOrderId() + " reserved products quantity "+ " ************* ");
//        CancelProductReservationCommand cancelProductReservationCommand =
//                new CancelProductReservationCommand(event.getProductId(),
//                        event.getOrderId(),
//                        event.getProductQuantity());
//        kafkaTemplate.send(productsCommandsTopicName, cancelProductReservationCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservationCancelledEvent event) {
        System.out.println("***** SAGA rollback transaction : ProductReservationCancelledEvent / orderId : " + event.getOrderId() + " ************* ");
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(event.getOrderId());
        kafkaTemplate.send(ordersCommandsTopicName, rejectOrderCommand);
        orderHistoryService.add(event.getOrderId(), OrderStatus.REJECTED);
    }
}