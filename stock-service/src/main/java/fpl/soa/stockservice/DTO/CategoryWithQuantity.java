package fpl.soa.stockservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWithQuantity {
    private String categoryId;
    private String categoryName;
    private String categoryImageBase64;

    private List<String> categoryColors;
    private List<String> categorySizes;

    private int productsQuantity;
    private List<Long> categoryPrices;

}
