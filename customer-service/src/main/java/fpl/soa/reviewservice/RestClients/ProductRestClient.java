package fpl.soa.reviewservice.RestClients;


import fpl.soa.reviewservice.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "stock-service" , url = "${stock.service.url}")
public interface ProductRestClient {
    @GetMapping("/api/v1/products/{productId}/variant")
    Product getProduct(
            @PathVariable("productId") String productId,
            @RequestParam("size") String size,
            @RequestParam("color") String color
    );

//    @GetMapping("products/{productId}/productPrice")
//    Price getProductPrice(@PathVariable String productId , @RequestHeader(value = "Authorization", required = true) String authorizationHeader) ;


}
