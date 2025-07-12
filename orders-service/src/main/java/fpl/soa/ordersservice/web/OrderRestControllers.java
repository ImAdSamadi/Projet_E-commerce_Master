package fpl.soa.ordersservice.web;

import fpl.soa.ordersservice.dtos.CreateOrderRequest;
import fpl.soa.ordersservice.dtos.CreateOrderResponse;
import fpl.soa.ordersservice.entities.OrderEntity;
import fpl.soa.ordersservice.entities.OrderHistoryEntity;
import fpl.soa.ordersservice.models.Customer;
import fpl.soa.ordersservice.service.OrderHistoryService;
import fpl.soa.ordersservice.service.OrdersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("api/v1/orders")
@CrossOrigin(origins = "*")
public class OrderRestControllers {

    private OrdersService ordersService ;
    private OrderHistoryService orderHistoryService ;

    public OrderRestControllers(OrdersService ordersService, OrderHistoryService orderHistoryService) {
        this.ordersService = ordersService;
        this.orderHistoryService = orderHistoryService;
    }

//    @PostMapping
//    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
//        return ordersService.placeOrder(createOrderRequest) ;
//    }

    @PostMapping
    public String createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        return ordersService.placeOrder(createOrderRequest).getOrderId() ;
    }

    @GetMapping("/{orderId}/history")
    public List<OrderHistoryEntity> getOrderHistory(@PathVariable String orderId) {
        return orderHistoryService.findByOrderId(orderId) ;
    }
    @GetMapping("/customer/{orderId}")
    public Customer getCustomer(@PathVariable String orderId){
        return ordersService.getCustomerOfOrder(orderId) ;
    }
    @GetMapping("test/{customerId}")
    public Customer getCustomergg(@PathVariable String customerId){
        return ordersService.getCustomer(customerId) ;
    }

    @GetMapping("/customerOrders/{customerId}")
    public ResponseEntity<Page<OrderEntity>> getOrdersForCustomer(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderEntity> result = ordersService.getOrdersForCustomer(customerId, status, pageable);

        return ResponseEntity.ok(result);
    }

}