package fpl.soa.ordersservice.service;
import fpl.soa.ordersservice.dtos.CreateOrderRequest;
import fpl.soa.ordersservice.dtos.CreateOrderResponse;
import fpl.soa.ordersservice.entities.OrderEntity;
import fpl.soa.ordersservice.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersService {
    CreateOrderResponse placeOrder(CreateOrderRequest orderReq);
    void approveOrder(String orderId);
    OrderEntity getOrderWithCustomer(String orderId);
    OrderEntity getOrderById(String orderId);
    void rejectOrder(String orderId);
    Customer getCustomerOfOrder(String orderId);
    Customer getCustomer(String CustomerId);

    Page<OrderEntity> getOrdersForCustomer(String customerId, String status, Pageable pageable);

}
