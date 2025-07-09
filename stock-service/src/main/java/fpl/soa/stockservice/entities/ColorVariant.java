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
public class ColorVariant {
    private String color;           // e.g., "Red"
    private int quantity;               // stock level for this color+size combo
    private boolean selected;           // selected flag for this color+size combo
    private List<String> productImagesBas64;  // photos specific to this variant
}
