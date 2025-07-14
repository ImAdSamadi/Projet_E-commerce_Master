package fpl.soa.couponservice.web;

import fpl.soa.couponservice.entities.Coupon;
import fpl.soa.couponservice.entities.Price;
import fpl.soa.couponservice.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponRestController {

    private final CouponService couponService;

    // GET /api/coupons -> Get all coupons
    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getCoupons());
    }

    // DELETE /api/coupons/{code} -> Delete a coupon by code
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable String code) {
        couponService.DeleteCoupon(code);
        return ResponseEntity.noContent().build();
    }

    // POST /api/coupons/apply/{code} -> Apply a coupon by code
    @PostMapping("/apply/{code}")
    public ResponseEntity<Coupon> applyCoupon(@PathVariable String code) {
        Coupon appliedCoupon = couponService.applyCoupon(code);
        return ResponseEntity.ok(appliedCoupon);
    }

    // GET /api/coupons/amount/{code} -> Get the discount amount (Price) for a coupon
    @GetMapping("/amount/{code}")
    public ResponseEntity<Price> getCouponAmount(@PathVariable String code) {
        Price price = couponService.getCouponAmount(code);
        return ResponseEntity.ok(price);
    }

}

