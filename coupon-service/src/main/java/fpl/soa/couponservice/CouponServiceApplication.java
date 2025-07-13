package fpl.soa.couponservice;

import fpl.soa.couponservice.service.CouponService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CouponServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(CouponService couponService) {
        return args -> couponService.initCoupons();
    }

}
