package fpl.soa.stockservice.web;


import fpl.soa.stockservice.DTO.CategoryWithQuantity;
import fpl.soa.stockservice.entities.Category;
import fpl.soa.stockservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "*")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<CategoryWithQuantity> getCategoriesWithProductsQuantity(
            @RequestParam(defaultValue = "false") boolean isAdmin
    ) {
        return this.categoryService.findCategoriesWithProductsQuantity(isAdmin);
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category){
        return this.categoryService.createCategory(category);
    }
    @PutMapping
    public Category updateCategory(@RequestBody Category category){
        return this.categoryService.updateCategory(category);
    }

    @DeleteMapping("{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();  // 204 No Content on successful deletion
    }

}
