package fpl.soa.couponservice.service;

import fpl.soa.couponservice.entities.Coupon;
import fpl.soa.couponservice.entities.Price;

import java.util.List;

public interface CouponService {

    Coupon create(Coupon coupon);
    void DeleteCoupon(String code);
    Coupon applyCoupon(String code);
    List<Coupon> getCoupons();
    void initCoupons();
    Price getCouponAmount(String code);

}
