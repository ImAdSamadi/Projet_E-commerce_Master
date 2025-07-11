package fpl.soa.reviewservice.RestClients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "customer-service" , url = "${customer.service.url}")
public interface CustomerRestClient {

    @GetMapping("/api/v1/customers/image-base64")
    String getCustomerImageBase64ByEmail(@RequestParam("email") String email);


}
