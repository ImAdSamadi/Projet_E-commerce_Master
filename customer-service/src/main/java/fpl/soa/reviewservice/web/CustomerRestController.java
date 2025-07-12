package fpl.soa.reviewservice.web;

import fpl.soa.reviewservice.entities.Customer;
import fpl.soa.reviewservice.entities.ShoppingCart;
import fpl.soa.reviewservice.exceptions.CustomerNotFoundException;
import fpl.soa.reviewservice.model.AddItemRequest;
import fpl.soa.reviewservice.service.CustomerService;
import fpl.soa.reviewservice.service.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin(origins = "*")
public class CustomerRestController {

    private ShoppingCartService cartService ;
    private CustomerService customerService ;

     public CustomerRestController(ShoppingCartService cartService, CustomerService customerService) {
        this.cartService = cartService;
         this.customerService = customerService;
     }

    @GetMapping("{customerId}")
    public Customer getCustomerById(@PathVariable String customerId) {
        return customerService.getCustomer(customerId) ;
    }

    @GetMapping("cart/{customerId}")
    public ShoppingCart getCustomerCartById(@PathVariable String customerId) {
        return customerService.getCustomer(customerId).getShoppingCart() ;
    }

    @GetMapping("/all")
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers() ;
    }



    @PostMapping
    public ShoppingCart addProductToCart(@RequestBody AddItemRequest addItemRequest){
        try {
            return this.cartService.addItemToCart(addItemRequest) ;
        } catch (CustomerNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @DeleteMapping("{customerId}/{productId}/{size}/{color}")
    public ShoppingCart deleteItemFromCart(
            @PathVariable String customerId,
            @PathVariable String productId,
            @PathVariable String size,
            @PathVariable String color) {
        try {
            return this.cartService.removeItemFromCart(customerId , productId, size, color) ;
        } catch (CustomerNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/image-base64")
    public ResponseEntity<String> getCustomerImageBase64(@RequestParam String email) {
        String base64 = customerService.getCustomerProfilePictureBase64(email);
        return ResponseEntity.ok(base64);
    }


}
