package fpl.soa.couponservice.service;

import fpl.soa.couponservice.entities.Coupon;
import fpl.soa.couponservice.entities.Price;
import fpl.soa.couponservice.enums.Currency;
import fpl.soa.couponservice.repos.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public Coupon create(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public void DeleteCoupon(String code) {
        couponRepository.deleteByCode(code);
    }

    @Override
    public Coupon applyCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Coupon Not Found"));

        if (coupon.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Coupon Expired");
        }

        coupon.setAtivated(true);
        couponRepository.save(coupon);
        return coupon;
    }


    @Override
    public List<Coupon> getCoupons() {
        return couponRepository.findAll();
    }

    @Override
    public void initCoupons() {

        if (couponRepository.count() == 0) {

        for (int i = 1; i < 11; i++) {
            Coupon coupon = Coupon.builder()
                    .code(UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                    .discountAmount(Price.builder()
                            .currency(Currency.USD)
                            .symbol("$")
                            .price(i*10.0)
                            .build())
                    .expirationDate(LocalDate.now().plusDays(30))
                    .build();

            couponRepository.save(coupon);

        }

        }

    }


    @Override
    public Price getCouponAmount(String code) {
        return couponRepository.findByCode(code)
                .map(Coupon::getDiscountAmount)
                .orElseThrow(() -> new RuntimeException("Coupon with Code '" + code + "' Not Found"));
    }


}
