package fpl.soa.ordersservice.restClient;

import fpl.soa.ordersservice.models.Customer;
import fpl.soa.ordersservice.models.Price;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "COUPON-SERVICE" , url = "${coupon.service.url}")
public interface CouponRestClient {

    @GetMapping("/api/v1/coupons/amount/{code}")
    Price getCouponAmount(@PathVariable("code") String code);

    @DeleteMapping("/api/v1/coupons/{code}")
    void deleteCoupon(@PathVariable("code") String code);

}
