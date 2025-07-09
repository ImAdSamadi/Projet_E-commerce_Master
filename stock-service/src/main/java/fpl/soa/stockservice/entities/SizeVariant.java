package fpl.soa.stockservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SizeVariant {
    private String size;               // e.g., "128GB"
    private Price productPrice;            // price depends on size
    private String description;            // description depends on size
    private Dimension dimension;           // dimension depends on size

    private List<ColorVariant> colorVariants;  // list of color variants for this size
}
