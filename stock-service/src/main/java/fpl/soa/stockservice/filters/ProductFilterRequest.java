package fpl.soa.stockservice.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterRequest {
    private String categoryId;
    private List<String> priceRanges;   // e.g. ["0-50", "50-100"]
    private List<String> sizes;         // e.g. ["S", "M", "L"]
    private List<String> colors;        // e.g. ["Red", "Blue"]
    private boolean admin;              // if false, filter by colorVariant.selected == true
}

