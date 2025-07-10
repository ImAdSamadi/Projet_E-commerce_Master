package fpl.soa.stockservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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
    private List<Double> categoryPrices;

    private List<Map<String, Object>> categoryProductsSizesWithCount;
    private List<Map<String, Object>> categoryProductsColorsWithCount;
    private List<Map<String, Object>> categoryPricesWithCount;

}
