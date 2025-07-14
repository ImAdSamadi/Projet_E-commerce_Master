package fpl.soa.ordersservice.service;

import fpl.soa.common.events.OrderApprovedEvent;
import fpl.soa.common.events.OrderCreatedEvent;
import fpl.soa.common.types.OrderStatus;
import fpl.soa.ordersservice.dtos.CreateOrderRequest;
import fpl.soa.ordersservice.dtos.CreateOrderResponse;
import fpl.soa.ordersservice.entities.OrderEntity;
import fpl.soa.ordersservice.entities.OrderItem;
import fpl.soa.ordersservice.mappers.IMapper;
import fpl.soa.ordersservice.models.Customer;
import fpl.soa.ordersservice.models.Product;
import fpl.soa.ordersservice.models.ShoppingCart;
import fpl.soa.ordersservice.models.ShoppingCartItem;
import fpl.soa.ordersservice.repositories.OrderRepo;
import fpl.soa.ordersservice.restClient.CouponRestClient;
import fpl.soa.ordersservice.restClient.CustomerRestClient;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrdersServiceImpl implements OrdersService {

    private OrderRepo orderRepo  ;
    private IMapper mapper ;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private String ordersEventsTopicName;
    private CustomerRestClient customerRestClient ;
    private final CouponRestClient couponRestClient ;

    public OrdersServiceImpl(OrderRepo orderRepo, IMapper mapper, KafkaTemplate<String, Object> kafkaTemplate, @Value("${orders.events.topic.name}") String ordersEventsTopicName, CustomerRestClient customerRestClient, CouponRestClient couponRestClient) {
        this.orderRepo = orderRepo;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
        this.ordersEventsTopicName = ordersEventsTopicName;
        this.customerRestClient = customerRestClient;
        this.couponRestClient = couponRestClient;
    }


    @Override
    public CreateOrderResponse placeOrder(CreateOrderRequest orderReq) {

        String token = getToken();

        // 1. Fetch Customer Shopping Cart
        Customer customer = customerRestClient
                .getCustomerCart(orderReq.getCustomerId(), token);

        ShoppingCart cart = customer.getShoppingCart();

        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty shopping cart.");
        }

        // 2. Convert cart items to OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (ShoppingCartItem cartItem : cart.getItems()) {
            if (cartItem.isSelected()) {
                Product product = cartItem.getProduct(); // already embedded
                int quantity = cartItem.getQuantity();

                OrderItem item = new OrderItem();
                item.setProductId(product.getProductId());
                item.setName(product.getName());
                item.setPriceAtPurchase(product.getProductPrice());
                item.setQuantity(quantity);
                item.setOriginLocation(product.getOriginLocation());
                item.setPickedColor(product.getPickedColor());
                item.setPickedSize(product.getPickedSize());
                item.setProductImagesBase64(product.getProductImagesBas64());

                orderItems.add(item);

                total += product.getProductPrice().getPrice() * quantity;
            }
        }

        Double couponAmount = 0.0;
        if (orderReq.getCouponCode() != null && !orderReq.getCouponCode().trim().isEmpty()) {
            couponAmount = couponRestClient.getCouponAmount(orderReq.getCouponCode())
                    .getPrice();
        }

        // 3. Create OrderEntity
        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setCustomerId(orderReq.getCustomerId());
        order.setProducts(orderItems);
        order.setTotalPrice(total);
        order.setStatus(OrderStatus.CREATED);
        order.setShippingAddress(orderReq.getShippingAddress());
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
        order.setDiscountAmount(couponAmount);

        orderRepo.save(order);

        // 4. Emit event to start Saga (without product list)
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getOrderId())
                .customerId(orderReq.getCustomerId())
                .customerEmail(customer.getEmail())
                .shippingAddress(orderReq.getShippingAddress())
                .customerFirstName(customer.getFirstName())
                .customerLastName(customer.getLastName())
                .receiverFullName(orderReq.getReceiverFullName())
                .receiverEmail(orderReq.getReceiverEmail())
                .couponCode(orderReq.getCouponCode())

                .build();

        kafkaTemplate.send(ordersEventsTopicName, event);

        // 5. Return basic order info
        return CreateOrderResponse.builder()
                .orderId(order.getOrderId())
                .customerId(customer.getCustomerId())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }



    @Override
    public void approveOrder(String orderId) {
        OrderEntity orderEntity = orderRepo.findById(orderId).orElse(null);
        Assert.notNull(orderEntity, "No order is found with id " + orderId + " in the database table");
        orderEntity.setStatus(OrderStatus.APPROVED);
        orderRepo.save(orderEntity);
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(orderId);
        kafkaTemplate.send(ordersEventsTopicName, orderApprovedEvent);
    }

    @Override
    public OrderEntity getOrderWithCustomer(String orderId) {
//        OrderEntity orderById = getOrderById(orderId);
//        Customer customer = customerRestClient.getCustomer(orderById.getCustomerId() , getToken());
//        System.out.println(customer);
//        Product product = productRestClient.getProduct(orderById.getProductId() , getToken());
//        orderById.setCustomer(customer);
//        orderById.setProduct(product);
//        return orderById;
        return null;

    }

    @Override
    public OrderEntity getOrderById(String orderId) {
        return orderRepo.findById(orderId).orElse(null);
    }

    @Override
    public void rejectOrder(String  orderId) {
        OrderEntity orderEntity = orderRepo.findById(orderId).orElse(null);
        Assert.notNull(orderEntity, "No order found with id: " + orderId);
        orderEntity.setStatus(OrderStatus.REJECTED);
        orderRepo.save(orderEntity);
    }

    @Override
    public Customer getCustomerOfOrder(String orderId) {
        OrderEntity orderEntity = orderRepo.findById(orderId).orElse(null);
        return customerRestClient.getCustomerCart(orderEntity.getCustomerId() , getToken());
    }

    @Override
    public Customer getCustomer(String customerId) {
        return customerRestClient.getCustomerCart(customerId , getToken());
    }

    @Override
    public Page<OrderEntity> getOrdersForCustomer(String customerId, String status, Pageable pageable) {
        Page<OrderEntity> orderPage;

        if (status != null && !status.isBlank()) {
            OrderStatus orderStatus;
            try {
                orderStatus = OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + status);
            }
            orderPage = orderRepo.findByCustomerIdAndStatus(customerId, orderStatus, pageable);
        } else {
            orderPage = orderRepo.findByCustomerId(customerId, pageable);
        }

        // Map each OrderEntity to OrderResponse DTO
        return orderPage;
    }

    private String getToken(){
        KeycloakSecurityContext context = (KeycloakSecurityContext) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String token ="bearer "+ context.getTokenString();
        System.out.println("*****************************");
        return token;
    }
}
