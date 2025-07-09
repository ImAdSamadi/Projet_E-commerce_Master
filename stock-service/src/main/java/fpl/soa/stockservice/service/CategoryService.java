package fpl.soa.stockservice.service;

import fpl.soa.stockservice.DTO.CategoryWithQuantity;
import fpl.soa.stockservice.entities.Category;

import java.util.List;

public interface CategoryService {

    List<CategoryWithQuantity> findCategoriesWithProductsQuantity();

    Category createCategory(Category category);
    Category updateCategory(Category category);
    void deleteCategory(String categoryId);
    void initCategory();


}
