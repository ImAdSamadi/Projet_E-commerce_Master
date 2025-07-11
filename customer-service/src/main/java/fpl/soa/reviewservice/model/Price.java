package fpl.soa.reviewservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Price {
    private String currency ;
    private Double price ;
    private String symbol ;
}
