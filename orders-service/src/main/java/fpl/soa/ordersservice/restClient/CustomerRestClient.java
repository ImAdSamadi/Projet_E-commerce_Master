package fpl.soa.ordersservice.restClient;

import fpl.soa.ordersservice.models.Customer;
import fpl.soa.ordersservice.models.ShoppingCart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "CUSTOMER-SERVICE" , url = "${customer.service.url}")
public interface CustomerRestClient {

    @GetMapping("/api/v1/customers/{customerId}")
    Customer getCustomerCart(@PathVariable("customerId") String customerId , @RequestHeader(value = "Authorization", required = true) String authorizationHeader);

    @DeleteMapping("/api/v1/customers/{customerId}/cart/clear-selected")
    void clearSelectedItems(@PathVariable("customerId") String customerId);

}
