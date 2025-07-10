package fpl.soa.stockservice.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PriceRange {

    private Double min;
    private Double max;

}
