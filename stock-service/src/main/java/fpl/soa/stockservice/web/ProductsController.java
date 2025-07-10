package fpl.soa.stockservice.web;

import fpl.soa.stockservice.DTO.ProductVariantDetails;
import fpl.soa.stockservice.entities.PageInfo;
import fpl.soa.stockservice.entities.PriceRange;
import fpl.soa.stockservice.entities.Product;
import fpl.soa.stockservice.filters.ProductFilterRequest;
import fpl.soa.stockservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*")
public class ProductsController {

    private ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }



    @GetMapping("{size}")
    public PageInfo getProductsPageInfo(@PathVariable int size){
        return this.productService.getProductPageInfo(size);
    }

    @GetMapping("find/{productId}")
    public Product getProductById(@PathVariable String productId){
        return this.productService.getProductById(productId);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product){
        return this.productService.createProduct(product);
    }
    @PutMapping
    public Product update(@RequestBody Product product){
        return this.productService.updateProduct(product);
    }

    @GetMapping("/{productId}/variant")
    public ResponseEntity<ProductVariantDetails> getProductVariantBySizeAndColor(
            @PathVariable String productId,
            @RequestParam String size,
            @RequestParam String color
    ) {
        ProductVariantDetails variantDetails = productService.getProductBySizeAndColor(productId, size, color);
        return ResponseEntity.ok(variantDetails);
    }


    @GetMapping("all")
    public ResponseEntity<Page<Product>> getAllProductsWithFilters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> priceRanges,
            @RequestParam(defaultValue = "false") boolean admin,
            @RequestParam(required = false) String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        ProductFilterRequest request = new ProductFilterRequest();
        request.setPriceRanges(cleanPriceRanges(priceRanges)); // Clean range like ["100-200"]
        request.setAdmin(admin);

        Page<Product> result = productService.filterAllProductsWithVariants(request, pageable);
        return ResponseEntity.ok(result);
    }


    @GetMapping("by-category")
    public ResponseEntity<Page<Product>> getProductsByCategoryWithFilters(
            @RequestParam String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) List<String> sizes,
            @RequestParam(required = false) List<String> colors,
            @RequestParam(required = false) List<String> priceRanges,
            @RequestParam(defaultValue = "false") boolean admin,
            @RequestParam(required = false) String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        ProductFilterRequest request = new ProductFilterRequest();
        request.setCategoryId(categoryId);
        request.setSizes(sizes);
        request.setColors(colors);

        // ✅ Clean raw price ranges into expected "500-600" format
        List<String> cleanedPriceRanges = cleanPriceRanges(priceRanges);
        request.setPriceRanges(cleanedPriceRanges);

        request.setAdmin(admin);

        Page<Product> result = productService.filterProductsByCategoryWithVariants(request, pageable);
        return ResponseEntity.ok(result);
    }


    @GetMapping("search")
    public ResponseEntity<Page<Product>> getProductsByKeywordWithFilters(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) List<String> sizes,
            @RequestParam(required = false) List<String> colors,
            @RequestParam(required = false) List<String> priceRanges,
            @RequestParam(defaultValue = "false") boolean admin,
            @RequestParam(required = false) String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        ProductFilterRequest request = new ProductFilterRequest();
        request.setKeyword(keyword);
        request.setSizes(sizes);
        request.setColors(colors);
        request.setPriceRanges(cleanPriceRanges(priceRanges));
        request.setAdmin(admin);

        Page<Product> result = productService.filterProductsByKeywordWithVariants(request, pageable);
        return ResponseEntity.ok(result);
    }



    private Sort parseSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.by(Sort.Order.desc("addingDate")); // default fallback
        }

        String[] parts = sort.split(",");
        if (parts.length == 2) {
            String field = parts[0].trim();
            String direction = parts[1].trim().toLowerCase();
            if (direction.equals("desc")) {
                return Sort.by(Sort.Order.desc(field));
            } else if (direction.equals("asc")) {
                return Sort.by(Sort.Order.asc(field));
            }
        }

        return Sort.by(sort); // fallback to unspecific sort
    }



    private List<String> cleanPriceRanges(List<String> rawRanges) {
        List<String> cleaned = new ArrayList<>();
        if (rawRanges == null) return cleaned;

        for (String raw : rawRanges) {
            try {
                String noDollar = raw.replaceAll("[$]", "").trim(); // "$500 - $600" → "500 - 600"
                String[] parts = noDollar.split("-");
                if (parts.length == 2) {
                    String min = parts[0].trim();
                    String max = parts[1].trim();
                    cleaned.add(min + "-" + max); // e.g., "500-600"
                }
            } catch (Exception e) {
                // Optionally log or ignore
            }
        }

        return cleaned;
    }




}