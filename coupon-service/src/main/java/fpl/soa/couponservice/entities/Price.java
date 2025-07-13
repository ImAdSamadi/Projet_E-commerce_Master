package fpl.soa.couponservice.entities;

import fpl.soa.couponservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Price {

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private Double price;

    private String symbol;
}

